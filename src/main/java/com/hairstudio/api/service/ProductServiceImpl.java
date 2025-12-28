package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.orders.OrderItemDetailDTO;
import com.hairstudio.api.dto.orders.OrderSummaryDTO;
import com.hairstudio.api.dto.orders.PagedOrdersDTO;
import com.hairstudio.api.dto.products.PagedProductsDTO;
import com.hairstudio.api.dto.products.ProductCreateDTO;
import com.hairstudio.api.dto.products.ProductDisplayDTO;
import com.hairstudio.api.dto.products.BuyProductDTO;
import com.hairstudio.api.dto.products.ProductUpdateDTO;
import com.hairstudio.api.dto.products.UrlResponseDTO;
import com.hairstudio.api.errors.BrandErrors;
import com.hairstudio.api.errors.OrderErrors;
import com.hairstudio.api.errors.PaymentStatusErrors;
import com.hairstudio.api.errors.ProductErrors;
import com.hairstudio.api.errors.ProductTypeErrors;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.ValidationErrors;
import com.hairstudio.api.model.entity.OrderItemId;
import com.hairstudio.api.model.entity.Product;
import com.hairstudio.api.model.entity.Order;
import com.hairstudio.api.model.entity.OrderItem;
import com.hairstudio.api.model.entity.PaymentStatus;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.model.enums.OrderStatusEnum;
import com.hairstudio.api.model.enums.PaymentStatusEnum;
import com.hairstudio.api.model.enums.RoleEnum;
import com.hairstudio.api.repository.OrderStatusRepository;
import com.hairstudio.api.repository.UserRepository;
import com.hairstudio.api.repository.ProductRepository;
import com.hairstudio.api.repository.PaymentStatusRepository;
import com.hairstudio.api.repository.BrandRepository;
import com.hairstudio.api.repository.ProductTypeRepository;
import com.hairstudio.api.repository.OrderRepository;
import com.hairstudio.api.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hairstudio.api.security.CurrentUserContext;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final CurrentUserContext currentUserContext;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final BrandRepository brandRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final MessageRepository messageRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Value("${frontend.url}")
    private String frontendUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResultWithValue<PagedProductsDTO> getProducts(int page, int rowsPerPage, String search, Short brand, Short type, int minPrice, int maxPrice, String sortOrder) {
        if (page < 1 || rowsPerPage < 1) {
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);
        }

        var products = productRepository.findByIsActiveTrue();

        var query = products.stream()
                .filter(p -> p.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0 &&
                        p.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) <= 0);

        if (brand != null)
            query = query.filter(p -> p.getBrand() != null && Objects.equals(p.getBrand().getBrandId(), brand));

        if (type != null)
            query = query.filter(p -> p.getProductType() != null && Objects.equals(p.getProductType().getProductTypeId(), type));

        if (search != null && !search.isBlank())
            query = query.filter(p -> p.getName().contains(search) || p.getDescription().contains(search));

        if (sortOrder != null && !sortOrder.isBlank()) {
            if (sortOrder.equalsIgnoreCase("asc")) {
                query = query.sorted(Comparator.comparing(Product::getPrice));
            } else if (sortOrder.equalsIgnoreCase("popularity")) {
                query = query.sorted(Comparator.comparing(Product::getNumberOfPurchases).reversed());
            } else {
                query = query.sorted(Comparator.comparing(Product::getPrice).reversed());
            }
        }

        List<ProductDisplayDTO> productsDto = query
                .sorted(Comparator.comparing(Product::getSequenceNumber))
                .skip((long) (page - 1) * rowsPerPage)
                .limit(rowsPerPage)
                .map(p -> new ProductDisplayDTO(
                        p.getProductId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock(),
                        p.getImage(),
                        p.getBrand() != null ? p.getBrand().getBrandId() : null,
                        p.getProductType() != null ? p.getProductType().getProductTypeId() : null,
                        p.getBrand() != null ? p.getBrand().getName() : null,
                        p.getProductType() != null ? p.getProductType().getName() : null,
                        p.getSequenceNumber()
                ))
                .toList();

        return ResultWithValue.success(new PagedProductsDTO(products.size(), productsDto));
    }

    @Override
    public ResultWithValue<List<ProductDisplayDTO>> getMostPopularProducts() {
        List<ProductDisplayDTO> products = productRepository.findByIsActiveTrue().stream()
                .sorted((a, b) -> Integer.compare(b.getNumberOfPurchases(), a.getNumberOfPurchases()))
                .limit(5)
                .map(p -> new ProductDisplayDTO(
                        p.getProductId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock(),
                        p.getImage(),
                        p.getBrand() != null ? p.getBrand().getBrandId() : null,
                        p.getProductType() != null ? p.getProductType().getProductTypeId() : null,
                        p.getBrand() != null ? p.getBrand().getName() : null,
                        p.getProductType() != null ? p.getProductType().getName() : null,
                        p.getSequenceNumber()
                ))
                .toList();

        return ResultWithValue.success(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultWithValue<PagedOrdersDTO> getOrders(int page, int rowsPerPage) {
        if (page < 1 || rowsPerPage < 1) {
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);
        }

        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());

        List<Order> allOrders = orderRepository.findAll(Sort.by(
                Sort.Order.asc("orderStatus.name"),
                Sort.Order.desc("orderId")
        ));

        List<Order> filteredOrders;

        if (isAdminOrEmployee) {
            filteredOrders = allOrders;
        } else {
            filteredOrders = allOrders.stream()
                    .filter(order -> order.getUser() != null &&
                            order.getUser().getUserId().equals(currentUserContext.getUserId()))
                    .toList();
        }

        int totalCount = filteredOrders.size();

        List<Order> pagedOrders = filteredOrders.stream()
                .sorted(Comparator.comparingInt((Order o) -> o.getOrderStatus().getOrderStatusId())
                .thenComparing(Order::getOrderId, Comparator.reverseOrder()))
                .skip((long) (page - 1) * rowsPerPage)
                .limit(rowsPerPage)
                .toList();

        List<OrderSummaryDTO> ordersForTable = pagedOrders.stream()
                .map(o -> new OrderSummaryDTO(
                        o.getOrderId(),
                        o.getUser().getFirstName() + " " + o.getUser().getLastName(),
                        o.getOrderStatus().getOrderStatusId(),
                        o.getPaymentStatus().getName(),
                        o.getPaidAt() != null
                                ? LocalDateTime.ofInstant(o.getPaidAt(), ZoneId.systemDefault())
                                : null,
                        o.getOrderItems().stream()
                                .map(oi -> new OrderItemDetailDTO(
                                        oi.getProduct().getName(),
                                        oi.getPrice(),
                                        oi.getQuantity()
                                ))
                                .toList()
                ))
                .toList();

        return ResultWithValue.success(new PagedOrdersDTO(totalCount, ordersForTable));
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_PRODUCT")
    public ResultWithoutValue createProduct(ProductCreateDTO dto) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var brandOpt = brandRepository.findById(dto.getBrandId());
        if (brandOpt.isEmpty() || !brandOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(BrandErrors.BRAND_NOT_FOUND);
        }
        var brand = brandOpt.get();

        var productTypeOpt = productTypeRepository.findById(dto.getProductTypeId());
        if (productTypeOpt.isEmpty() || !productTypeOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(ProductTypeErrors.PRODUCT_TYPE_NOT_FOUND);
        }
        var productType = productTypeOpt.get();

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setBrand(brand);
        product.setProductType(productType);
        product.setSequenceNumber(dto.getSequenceNumber());
        product.setIsActive(true);
        product.setNumberOfPurchases(0);
        product.setCreatedAt(Instant.now());
        try {
            product.setImage(dto.getImage().getBytes());
        } catch (IOException e) {
            log.error("Failed to read product image for product name={}", product.getName(), e);
            return ResultWithoutValue.failure(ValidationErrors.IMAGE_READ_ERROR);
        }

        productRepository.save(product);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content("User " + user.getFirstName() + " " + user.getLastName() +
                        " created a product " + dto.getName() + ".").build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "BUY_PRODUCTS")
    public ResultWithValue<UrlResponseDTO> buyProducts(List<BuyProductDTO> dtoList) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        Stripe.apiKey = stripeApiKey;

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        order.setPaymentStatusId(PaymentStatusEnum.UNPAID.getCode());
        order.setOderStatusId(OrderStatusEnum.Pending.getCode());
        order.setOrderItems(new ArrayList<>());

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (BuyProductDTO dto : dtoList) {
            Optional<Product> productOpt = productRepository.findById(dto.getProductId());
            if(productOpt.isEmpty())
                return ResultWithValue.failure(ProductErrors.PRODUCT_NOT_FOUND);
            var product = productOpt.get();

            product.setStock(product.getStock() - dto.getQuantity());
            product.setNumberOfPurchases(product.getNumberOfPurchases() + dto.getQuantity());

            OrderItem item = new OrderItem();
            OrderItemId itemId = new OrderItemId();
            itemId.setProductId(product.getProductId());
            item.setId(itemId);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            item.setOrder(order);
            order.getOrderItems().add(item);

            totalPrice = totalPrice.add(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) dto.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setUnitAmount(product.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(product.getName())
                                                    .build())
                                            .build()
                            )
                            .build()
            );
        }

        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/orders?status=success")
                .setCancelUrl(frontendUrl + "/orders?status=cancel")
                .build();

        try {
            Session session = Session.create(params);
            UrlResponseDTO urlResponse = new UrlResponseDTO(session.getUrl());
            return ResultWithValue.success(urlResponse);
        } catch (Exception e) {
            log.error("Failed to create Stripe session for userId={} and orderId={}", currentUserContext.getUserId(), order.getOrderId(), e);
            return ResultWithValue.failure(ValidationErrors.INVALID_DATA);
        }
    }

    @Override
    public Object getSessionDetails(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return Map.of(
                    "id", session.getId(),
                    "customerEmail", session.getCustomerEmail(),
                    "amountTotal", session.getAmountTotal(),
                    "paymentStatus", session.getPaymentStatus(),
                    "metadata", session.getMetadata()
            );
        } catch (Exception e) {
            log.error("Failed to retrieve Stripe session with id={}", sessionId, e);
            return Map.of("error", e.getMessage());
        }
    }

    @Override
    @Transactional
    @Auditable(action = "HANDLE_STRIPE_WEBHOOK")
    public ResultWithoutValue handleStripeWebhook(String json, String stripeSignature) {
        try {
            Event stripeEvent = Webhook.constructEvent(
                    json,
                    stripeSignature,
                    stripeWebhookSecret
            );

            if ("checkout.session.completed".equals(stripeEvent.getType())) {
                Session session = (Session) stripeEvent.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new RuntimeException("Invalid session object"));

                String orderIdStr = session.getMetadata().get("orderId");
                if (orderIdStr != null) {
                    Short orderId = Short.valueOf(orderIdStr);
                    Optional<Order> orderOpt = orderRepository.findById(orderId);
                    if (orderOpt.isPresent()) {
                        Order order = orderOpt.get();
                        if (!PaymentStatusEnum.PAID.name().equals(order.getPaymentStatus().getName())) {
                            Optional<PaymentStatus> paidStatusOpt = paymentStatusRepository.findByName(PaymentStatusEnum.PAID.name());
                            if(paidStatusOpt.isEmpty())
                                return ResultWithoutValue.failure(PaymentStatusErrors.PAYMENT_STATUS_NOT_FOUND);
                            var paidStatus = paidStatusOpt.get();
                            order.setPaymentStatus(paidStatus);
                            order.setPaidAt(Instant.now());
                            orderRepository.save(order);
                        }
                    }
                }
            }

            return ResultWithoutValue.success();
        } catch (Exception e) {
            log.error("Failed to handle Stripe webhook. Signature: {}", stripeSignature, e);
            return ResultWithoutValue.failure(ValidationErrors.INVALID_DATA);
        }
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_PRODUCT")
    public ResultWithoutValue updateProduct(short productId, ProductUpdateDTO dto) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var brandOpt = brandRepository.findById(dto.getBrandId());
        if (brandOpt.isEmpty()) {
            return ResultWithoutValue.failure(BrandErrors.BRAND_NOT_FOUND);
        }
        var brand = brandOpt.get();

        var productTypeOpt = productTypeRepository.findById(dto.getProductTypeId());
        if (productTypeOpt.isEmpty()) {
            return ResultWithoutValue.failure(ProductTypeErrors.PRODUCT_TYPE_NOT_FOUND);
        }
        var productType = productTypeOpt.get();

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResultWithoutValue.failure(ProductErrors.PRODUCT_NOT_FOUND);
        }
        Product product = productOpt.get();

        product.setName(dto.getName());
        product.setBrand(brand);
        product.setProductType(productType);
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setSequenceNumber(dto.getSequenceNumber());
        try {
            if(dto.getImage() != null)
                product.setImage(dto.getImage().getBytes());
        } catch (IOException e) {
            log.error("Failed to read product image for product name={}", product.getName(), e);
            return ResultWithoutValue.failure(ValidationErrors.IMAGE_READ_ERROR);
        }

        productRepository.save(product);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content("User " + user.getFirstName() + " " + user.getLastName() +
                        " updated a product " + dto.getName() + ".").build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "CHANGE_ORDER_STATUS")
    public ResultWithoutValue changeOrderStatus(short orderId, short orderStatusId) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var existingOrderOpt = orderRepository.findById(orderId);
        if (existingOrderOpt.isEmpty()) {
            return ResultWithoutValue.failure(OrderErrors.ORDER_NOT_FOUND);
        }
        var existingOrder = existingOrderOpt.get();

        var orderStatusOpt = orderStatusRepository.findByOrderStatusId(orderStatusId);
        if (orderStatusOpt.isEmpty()) {
            return ResultWithoutValue.failure(OrderErrors.ORDER_STATUS_NOT_FOUND);
        }
        var orderStatus = orderStatusOpt.get();

        existingOrder.setOrderStatus(orderStatus);

        var existingUserOpt = userRepository.findById(existingOrder.getUser().getUserId());
        if (existingUserOpt.isEmpty() || !existingUserOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var existingUser = existingUserOpt.get();

        orderRepository.save(existingOrder);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s changed %s %s's order to %s.",
                        user.getFirstName(),
                        user.getLastName(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        orderStatus.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_PRODUCT")
    public ResultWithoutValue deleteProduct(short productId) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResultWithoutValue.failure(ProductErrors.PRODUCT_NOT_FOUND);
        }
        Product product = productOpt.get();

        productRepository.findById(productId).ifPresent(p -> {
            p.setIsActive(false);
            productRepository.save(p);
        });

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content("User " + user.getFirstName() + " " + user.getLastName() +
                        " deleted a product " + product.getName() + ".").build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }
}
