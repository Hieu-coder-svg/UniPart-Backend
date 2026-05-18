package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.StudentScheduleRequest;
import com.unipart.unipart_backend.dto.response.StudentScheduleResponse;
import com.unipart.unipart_backend.entity.StudentSchedule;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.StudentScheduleMapper;
import com.unipart.unipart_backend.repository.StudentScheduleRepository;
import com.unipart.unipart_backend.repository.TimeSlotRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.StudentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentScheduleServiceImpl implements StudentScheduleService {

    private final StudentScheduleRepository studentScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final StudentScheduleMapper studentScheduleMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public  StudentScheduleResponse saveFullSchedule(StudentScheduleRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user =  userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        studentScheduleRepository.deleteAllByUserId((user.getId()));

        List<StudentSchedule> newEntities = new ArrayList<>();
        for (var dayReq : request.getSchedules()) {
            if (dayReq.getBusyTimeSlotIds() != null && !dayReq.getBusyTimeSlotIds().isEmpty()) {
                StudentSchedule entity = StudentSchedule.builder()
                        .userId(user.getId())
                        .dayOfWeek(dayReq.getDayOfWeek())
                        .busyTimeSlots(new HashSet<>(timeSlotRepository.findAllById(dayReq.getBusyTimeSlotIds())))
                        .build();
                newEntities.add(studentScheduleRepository.save(entity));
            }
        }
       return studentScheduleMapper.toResponse(user.getId(), newEntities);
    }
    @Override
    public StudentScheduleResponse getStudentSchedulesByUserId() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        List<StudentSchedule> entities = studentScheduleRepository.findByUserId(user.getId());
        return studentScheduleMapper.toResponse(user.getId(), entities);
    }
}

