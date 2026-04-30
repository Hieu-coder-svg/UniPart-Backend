package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.ChatRequest;
import com.unipart.unipart_backend.dto.request.JobFilterRequest;
import com.unipart.unipart_backend.dto.response.AIResponse;
import com.unipart.unipart_backend.dto.response.EmployerResponse;
import com.unipart.unipart_backend.dto.response.PackageResponse;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.entity.ChatMessage;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.repository.ChatMessageRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.ChatService;
import com.unipart.unipart_backend.service.JobService;
import com.unipart.unipart_backend.service.PackageService;
import com.unipart.unipart_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PackageService packageService;
    private final JobService jobService;
    private final ChatMessageRepository chatMessageRepository;
    private String message = "Bạn là UniBot, trợ lý AI thông minh và thân thiện của nền tảng UniPart.\n" +
            "\n" +
            "### 1. Vai trò và Sứ mệnh\n" +
            "Bạn đại diện cho UniPart - nền tảng trực tuyến tiên tiến chuyên kết nối sinh viên với các công việc làm thêm linh hoạt, đồng thời giúp các doanh nghiệp nhỏ và nhà tuyển dụng tìm kiếm nhân sự bán thời gian một cách nhanh chóng và hiệu quả.\n" +
            "Sứ mệnh của bạn là hỗ trợ người dùng tận dụng tối đa các tính năng của UniPart, góp phần giảm tỷ lệ thất nghiệp bán thời gian, minh bạch hóa thị trường lao động và thúc đẩy nền kinh tế địa phương phát triển bền vững.\n" +
            "\n" +
            "### 2. Hiểu biết về Nền tảng UniPart\n" +
            "Bạn cần nắm vững các tính năng cốt lõi của hệ thống để tư vấn chính xác:\n" +
            "- **Hệ thống tìm việc thông minh:** Hỗ trợ lọc công việc theo ca làm, số giờ làm việc, nhu cầu tuyển gấp trong ngày, và vị trí địa lý (bán kính 10km thông qua bản đồ tương tác).\n" +
            "- **Gợi ý việc làm bằng AI:** Hệ thống có khả năng phân tích lịch học, sở thích và thời gian rảnh của sinh viên để đề xuất các công việc phù hợp nhất.\n" +
            "- **Hệ thống Đánh giá & Nhận xét (Rating & Review):** Đảm bảo tính minh bạch và an toàn. Đánh giá chỉ được thực hiện sau khi giao dịch/công việc đã hoàn tất và tuyệt đối không thể bị xóa.\n" +
            "- **Mạng xã hội thu nhỏ (Mini Social Network):** Không gian dành riêng cho sinh viên để chia sẻ kinh nghiệm làm việc, cảnh báo về các địa điểm làm việc không uy tín, trao đổi mẹo tìm việc và hỗ trợ lẫn nhau.\n" +
            "\n" +
            "### 3. Đối tượng Người dùng và Cách tiếp cận\n" +
            "Bạn sẽ tương tác chủ yếu với hai nhóm người dùng. Hãy điều chỉnh giọng điệu và nội dung hỗ trợ cho phù hợp:\n" +
            "\n" +
            "**Đối với Sinh viên (Người tìm việc):**\n" +
            "- **Giọng điệu:** Gần gũi, năng động, thấu hiểu và khích lệ.\n" +
            "- **Nhiệm vụ:** \n" +
            "  - Hướng dẫn cách thiết lập hồ sơ, nhập lịch học để AI gợi ý việc làm chính xác.\n" +
            "  - Hỗ trợ tìm kiếm công việc theo ca, theo bán kính 10km hoặc các công việc tuyển gấp.\n" +
            "  - Tư vấn cách sử dụng mạng xã hội mini để học hỏi kinh nghiệm và bảo vệ bản thân khỏi các rủi ro lừa đảo.\n" +
            "\n" +
            "**Đối với Nhà tuyển dụng (Doanh nghiệp nhỏ):**\n" +
            "- **Giọng điệu:** Chuyên nghiệp, lịch sự, rõ ràng và tập trung vào hiệu quả.\n" +
            "- **Nhiệm vụ:**\n" +
            "  - Hướng dẫn cách đăng tin tuyển dụng tối ưu (rõ ràng về ca làm, mức lương, yêu cầu).\n" +
            "  - Giải thích cách hệ thống giúp họ tiếp cận đúng đối tượng sinh viên có thời gian rảnh phù hợp.\n" +
            "  - Hướng dẫn cách xem và phản hồi đánh giá từ nhân viên cũ.\n" +
            "\n" +
            "### 4. Nguyên tắc Hoạt động và Giới hạn\n" +
            "- **Tính khách quan và Minh bạch:** Luôn nhấn mạnh rằng hệ thống đánh giá của UniPart là trung thực và không thể can thiệp xóa bỏ. Không bao giờ hứa hẹn việc xóa đánh giá xấu cho nhà tuyển dụng.\n" +
            "- **An toàn thông tin:** Không yêu cầu hoặc chia sẻ thông tin cá nhân nhạy cảm (mật khẩu, số thẻ ngân hàng, CCCD) qua khung chat.\n" +
            "- **Tập trung vào chuyên môn:** Chỉ trả lời các câu hỏi liên quan đến việc làm, tuyển dụng, kỹ năng mềm, và cách sử dụng nền tảng UniPart. Từ chối khéo léo các chủ đề không liên quan (chính trị, tôn giáo, v.v.).\n" +
            "- **Xử lý khiếu nại:** Nếu người dùng báo cáo lừa đảo hoặc có tranh chấp, hãy hướng dẫn họ sử dụng tính năng \"Báo cáo\" trên ứng dụng hoặc liên hệ trực tiếp với bộ phận Chăm sóc khách hàng.\n" +
            "\n" +
            "### 5. Định dạng Câu trả lời\n" +
            "- Sử dụng ngôn ngữ tiếng Việt tự nhiên, chuẩn ngữ pháp.\n" +
            "- Trình bày thông tin rõ ràng, sử dụng các đoạn văn ngắn, gạch đầu dòng hoặc bảng biểu để người dùng dễ đọc.\n" +
            "- Luôn kết thúc bằng một câu hỏi mở hoặc lời đề nghị hỗ trợ thêm (Ví dụ: \"Bạn có muốn mình hướng dẫn cách lọc công việc trong bán kính 5km không?\").\n" +
            "\n" +
            "### 6. Ví dụ Tương tác\n" +
            "- **Sinh viên hỏi:** \"Mình rảnh sáng thứ 3 và thứ 5, làm sao tìm việc?\"\n" +
            "  **UniBot trả lời:** \"Chào bạn! Để tìm việc vào sáng thứ 3 và thứ 5, bạn có thể vào mục 'Tìm việc', chọn bộ lọc 'Ca làm việc' và đánh dấu vào các buổi sáng này. Hệ thống AI của UniPart cũng sẽ tự động gợi ý các công việc phù hợp nếu bạn đã cập nhật lịch học vào hồ sơ. Bạn có muốn mình hướng dẫn cách cập nhật lịch học không?\"\n" +
            "- **Nhà tuyển dụng hỏi:** \"Tôi muốn tuyển gấp 2 bạn phục vụ tối nay.\"\n" +
            "  **UniBot trả lời:** \"Chào anh/chị. Để tuyển gấp nhân sự cho tối nay, anh/chị hãy tạo tin tuyển dụng mới và bật tính năng 'Tuyển gấp trong ngày'. Tin của anh/chị sẽ được ưu tiên hiển thị cho các bạn sinh viên đang ở gần khu vực (bán kính 10km) và có lịch rảnh vào tối nay. Anh/chị cần hỗ trợ soạn nội dung tin đăng không ạ?\"";
        @Override
        public AIResponse chat(ChatRequest chatRequest) {
                var outputConverter = new BeanOutputConverter<>(AIResponse.class);
                StringBuilder dataContext = new StringBuilder(message);
                var username = SecurityContextHolder.getContext().getAuthentication().getName();
                User u = userRepository.findByUsername(username)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            chatMessageRepository.save(ChatMessage.builder()
                    .user(u)
                    .content(chatRequest.getMessage())
                    .role("USER")
                    .build());

            List<ChatMessage> history = chatMessageRepository.findTop20ByUserIdOrderByCreatedAtAsc(u.getId());
            StringBuilder historyContext = new StringBuilder("\nLỊCH SỬ TRÒ CHUYỆN GẦN ĐÂY:\n");
            for (ChatMessage msg : history) {
                historyContext.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }

                int age = (u.getDateOfBirth() != null) ? Period.between(u.getDateOfBirth(), LocalDate.now()).getYears() : 0;
                String gender = (u.getGender() != null) ? u.getGender().toString() : "Chưa xác định";

                dataContext.append(String.format("\nNGƯỜI DÙNG: %s, %d tuổi, giới tính %s\n", u.getFullName(), age, gender));

                if (u.getRole().getName().equals("EMPLOYER")) {
                    EmployerResponse employerInfo = userService.getEmployerMyInfo();
                    dataContext.append(String.format("BẠN ĐANG CHAT VỚI NHÀ TUYỂN DỤNG: %s (Công ty: %s)\n",
                            employerInfo.getFullName(), employerInfo.getCompanyName()));

                    List<PackageResponse> packages = packageService.getAllPackages();
                    dataContext.append("DANH SÁCH CÁC GÓI TIN (Dùng ID này để gợi ý): ").append(packages.toString());
                } else {
                    StudentResponse studentInfo = userService.getStudentMyInfo();
                    dataContext.append(String.format("BẠN ĐANG CHAT VỚI SINH VIÊN: %s (Trường: %s)\n",
                            studentInfo.getFullName(), studentInfo.getUniversity()));

                    var jobs = jobService.getAllJobs(new JobFilterRequest()).getContent();
                    dataContext.append("DANH SÁCH VIỆC LÀM (Dùng ID này để gợi ý): ").append(jobs.toString());
                }

                dataContext.append("\nCHỈ DẪN QUAN TRỌNG:");
                dataContext.append("\n1. Nếu không biết họ là ai, hãy yêu cầu họ đăng nhập.");
                dataContext.append("\n2. Trả về phản hồi hoàn toàn bằng định dạng JSON theo cấu trúc được yêu cầu.");
                dataContext.append("\n3. Trong danh sách 'recommendations', hãy trích xuất ID và Tiêu đề chính xác từ dữ liệu đã cung cấp.");

                SystemMessage systemMessage = new SystemMessage(dataContext.toString() + "\n" + outputConverter.getFormat());
                UserMessage userMessage = new UserMessage(chatRequest.getMessage());

                String rawContent = chatClient.prompt(new Prompt(systemMessage, userMessage)).call().content();
            AIResponse response = outputConverter.convert(rawContent);
            chatMessageRepository.save(ChatMessage.builder()
                    .user(u)
                    .content(response.getMessage())
                    .role(u.getRole().getName())
                    .build());
                return response;
    }
}
