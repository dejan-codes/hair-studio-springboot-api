package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.reservations.EmployeeReservationCreateDTO;
import com.hairstudio.api.dto.reservations.ReservationDetailsDTO;
import com.hairstudio.api.dto.reservations.ReservationSummaryDTO;
import com.hairstudio.api.dto.reservations.UserReservationCreateDTO;
import com.hairstudio.api.errors.ServiceErrors;
import com.hairstudio.api.model.enums.RoleEnum;
import com.hairstudio.api.errors.ReservationErrors;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.model.entity.Customer;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.model.entity.Reservation;
import com.hairstudio.api.model.entity.User;
import com.hairstudio.api.repository.CustomerRepository;
import com.hairstudio.api.repository.MessageRepository;
import com.hairstudio.api.repository.ReservationRepository;
import com.hairstudio.api.repository.ServiceRepository;
import com.hairstudio.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;

    @Value("${app.status.disable}")
    private String disableStatus;

    @Override
    @Transactional
    @Auditable(action = "CREATE_USER_RESERVATION")
    public ResultWithoutValue createUserReservation(Short tokenUserId, UserReservationCreateDTO dto) {
        var userOpt = userRepository.findByUserIdWithRoles(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var serviceOpt = serviceRepository.findById(dto.getServiceId());
        if (serviceOpt.isEmpty() || !serviceOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var service = serviceOpt.get();

        var employeeOpt = userRepository.findById(dto.getEmployeeId());
        if (employeeOpt.isEmpty() || !employeeOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var employee = employeeOpt.get();

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());
        if (service.getName().equals(disableStatus) && !isAdminOrEmployee)
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);

        var reservation = new Reservation();
        reservation.setService(service);
        reservation.setClientUser(user);
        reservation.setEmployee(employee);
        reservation.setDateFrom(dto.getDateFrom().atZone(ZoneId.systemDefault()).toInstant());
        reservation.setDateTo(dto.getDateTo().atZone(ZoneId.systemDefault()).toInstant());
        reservation.setNote(dto.getNote());
        reservation.setIsActive(true);
        reservationRepository.save(reservation);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User " + user.getFirstName() + " " + user.getLastName() + " created a reservation from " + dto.getDateFrom() + " to " + dto.getDateTo())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_EMPLOYEE_RESERVATION")
    public ResultWithoutValue createEmployeeReservation(Short tokenUserId, EmployeeReservationCreateDTO dto) {
        var userOpt = userRepository.findByUserIdWithRoles(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var serviceOpt = serviceRepository.findById(dto.getServiceId());
        if (serviceOpt.isEmpty() || !serviceOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(ServiceErrors.SERVICE_NOT_FOUND);
        }
        var service = serviceOpt.get();

        var employeeOpt = userRepository.findById(dto.getEmployeeId());
        if (employeeOpt.isEmpty() || !employeeOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var employee = employeeOpt.get();

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());
        if (service.getName().equals(disableStatus) && !isAdminOrEmployee)
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);

        var customer = Customer.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .build();
        customerRepository.save(customer);

        var reservation = new Reservation();
        reservation.setService(service);
        reservation.setEmployee(employee);
        reservation.setDateFrom(dto.getDateFrom().atZone(ZoneId.systemDefault()).toInstant());
        reservation.setDateTo(dto.getDateTo().atZone(ZoneId.systemDefault()).toInstant());
        reservation.setNote(dto.getNote());
        reservation.setIsActive(true);
        reservation.setClientCustomer(customer);
        reservationRepository.save(reservation);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("Employee " + user.getFirstName() + " " + user.getLastName() +
                        " created a reservation from " + dto.getDateFrom() + " to " + dto.getDateTo() +
                        " for client " + dto.getFullName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    public ResultWithValue<List<ReservationSummaryDTO>> getEmployeeReservations(Short tokenUserId, int employeeId,
                                                                                LocalDate from, LocalDate to) {
        var userOpt = userRepository.findByUserIdWithRoles(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        Instant fromInstant = from.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toInstant = to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());

        List<Reservation> reservations = reservationRepository.getReservationsByEmployeeAndDate(employeeId, fromInstant, toInstant);

        List<ReservationSummaryDTO> summary = reservations.stream()
                .map(r -> new ReservationSummaryDTO(
                        r.getService().getServiceId(),
                        r.getReservationId(),
                        r.getService() != null ? r.getService().getName() : "",
                        r.getClientCustomer() != null ? r.getClientCustomer().getFullName() :
                                r.getClientUser().getFirstName() + " " + r.getClientUser().getLastName(),
                        LocalDateTime.ofInstant(r.getDateFrom(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(r.getDateTo(), ZoneId.systemDefault()),
                        isAdminOrEmployee || (r.getClientCustomer() != null && user.getUserId().equals(r.getClientCustomer().getCustomerId()))
                ))
                .collect(Collectors.toList());

        return ResultWithValue.success(summary);
    }

    @Override
    public ResultWithValue<ReservationDetailsDTO> getReservationDetails(Short tokenUserId, short reservationId) {
        var userOpt = userRepository.findByUserIdWithRoles(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var reservationOpt = reservationRepository.getReservationWithDetails(reservationId);
        if (reservationOpt.isEmpty())
            return ResultWithValue.failure(ReservationErrors.RESERVATION_NOT_FOUND);
        var reservation = reservationOpt.get();

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());
        if (!isAdminOrEmployee && !reservation.getClientUser().getUserId().equals(user.getUserId()))
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);

        ReservationDetailsDTO dto = new ReservationDetailsDTO(
                reservation.getService().getName(),
                reservation.getClientCustomer() != null
                        ? reservation.getClientCustomer().getFullName()
                        : reservation.getClientUser().getFirstName() + " " + reservation.getClientUser().getLastName(),
                LocalDateTime.ofInstant(reservation.getDateFrom(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(reservation.getDateTo(), ZoneId.systemDefault()),
                reservation.getClientUser() != null ? reservation.getClientUser().getPhoneNumber() : null,
                reservation.getClientUser() != null ? reservation.getClientUser().getEmail() : "",
                reservation.getNote()
        );

        return ResultWithValue.success(dto);
    }

    @Override
    @Transactional
    @Auditable(action = "CANCEL_RESERVATION")
    public ResultWithoutValue cancelReservation(Short tokenUserId, short reservationId) {
        var userOpt = userRepository.findByUserIdWithRoles(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null)
            return ResultWithoutValue.failure(ReservationErrors.RESERVATION_NOT_FOUND);

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());
        if (!isAdminOrEmployee && !reservation.getClientUser().getUserId().equals(user.getUserId()))
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);

        reservation.setIsActive(false);
        reservationRepository.save(reservation);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User " + user.getFirstName() + " " + user.getLastName() +
                        " cancelled reservation from " + reservation.getDateFrom() + " to " + reservation.getDateTo())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    public byte[] exportCalendar(Instant from, Instant to) throws IOException {
        final ZoneId zone = ZoneId.systemDefault();
        LocalDate fromDate = from.atZone(zone).toLocalDate();
        LocalDate toDate = to.atZone(zone).toLocalDate();

        List<User> employees = userRepository.getEmployeesWithReservations(from, to, RoleEnum.EMPLOYEE.getRoleName());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<LocalTime> timeSlots = IntStream.range(0, (20 - 8) * 2)
                    .mapToObj(i -> LocalTime.of(8, 0).plusMinutes(i * 30L))
                    .toList();

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            for (User employee : employees) {
                String fullName = employee.getFirstName() + " " + employee.getLastName();
                String sheetName = fullName.length() > 31 ? fullName.substring(0, 31) : fullName;

                Sheet ws = workbook.createSheet(sheetName);

                List<LocalDate> days = fromDate.datesUntil(toDate.plusDays(1)).toList();

                Row headerRow = ws.createRow(0);
                for (int i = 0; i < days.size(); i++) {
                    Cell cell = headerRow.createCell(i + 1);
                    cell.setCellValue(days.get(i).format(DateTimeFormatter.ofPattern("EEEE, dd.MM")));
                    cell.setCellStyle(headerStyle);
                }

                for (int i = 0; i < timeSlots.size(); i++) {
                    Row row = ws.getRow(i + 1);
                    if (row == null) row = ws.createRow(i + 1);

                    Cell timeCell = row.createCell(0);
                    timeCell.setCellValue(timeSlots.get(i).format(DateTimeFormatter.ofPattern("HH:mm")));
                    timeCell.setCellStyle(headerStyle);
                }

                for (Reservation res : employee.getReservations()) {
                    LocalDate resDate = res.getDateFrom().atZone(zone).toLocalDate();

                    long dayOffset = ChronoUnit.DAYS.between(fromDate, resDate);
                    if (dayOffset < 0 || dayOffset >= days.size()) continue;

                    LocalTime resStartTime = res.getDateFrom().atZone(zone).toLocalTime();
                    LocalTime resEndTime = res.getDateTo().atZone(zone).toLocalTime();

                    int startIndex = IntStream.range(0, timeSlots.size())
                            .filter(i -> timeSlots.get(i).equals(resStartTime))
                            .findFirst().orElse(-1);

                    int endIndex = IntStream.range(0, timeSlots.size())
                            .filter(i -> timeSlots.get(i).equals(resEndTime))
                            .findFirst().orElse(-1);

                    if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) continue;

                    int rowStart = 1 + startIndex;
                    int rowEnd = 1 + endIndex - 1;
                    int col = 1 + (int) dayOffset;

                    String serviceName = (res.getService() != null && disableStatus.equals(res.getService().getName()))
                            ? "Disabled"
                            : res.getService() != null ? res.getService().getName() : "";

                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setWrapText(true);
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                    if ("Disabled".equals(serviceName)) {
                        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    } else {
                        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                    }
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);

                    Font cellFont = workbook.createFont();
                    cellFont.setFontHeightInPoints((short) 11);
                    cellFont.setBold(false);
                    cellStyle.setFont(cellFont);

                    if (rowStart < rowEnd) {
                        ws.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, col, col));
                    }

                    String timeText = String.format("%s - %s",
                            res.getDateFrom().atZone(zone).format(DateTimeFormatter.ofPattern("HH:mm")),
                            res.getDateTo().atZone(zone).format(DateTimeFormatter.ofPattern("HH:mm"))
                    );

                    String name = res.getClientCustomer() != null
                            ? res.getClientCustomer().getFullName()
                            : res.getClientUser().getFirstName() + " " + res.getClientUser().getLastName();

                    String content = String.format("%s\n%s\n%s", timeText, name, serviceName);

                    Row row = ws.getRow(rowStart);
                    if (row == null) row = ws.createRow(rowStart);
                    Cell cell = row.createCell(col);
                    cell.setCellValue(content);
                    cell.setCellStyle(cellStyle);

                    for (int r = rowStart; r <= rowEnd; r++) {
                        Row currentMergedRow = ws.getRow(r);
                        if (currentMergedRow == null) currentMergedRow = ws.createRow(r);

                        Cell mergedCell = currentMergedRow.getCell(col);
                        if (mergedCell == null) mergedCell = currentMergedRow.createCell(col);

                        mergedCell.setCellStyle(cellStyle);
                    }
                }

                for (int i = 0; i <= days.size(); i++) {
                    ws.autoSizeColumn(i);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
