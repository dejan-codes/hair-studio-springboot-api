package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {

    List<Role> findByRoleIdIn(List<Short> ids);
}