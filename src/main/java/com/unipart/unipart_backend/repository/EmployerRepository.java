package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface EmployerRepository extends JpaRepository<Employer,String> {
    Employer findByUser(User user);

    Employer findByUserUsername(String username);
}
