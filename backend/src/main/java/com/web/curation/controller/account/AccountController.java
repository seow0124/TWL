package com.web.curation.controller.account;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import com.web.curation.dao.user.UserDao;
import com.web.curation.model.BasicResponse;
import com.web.curation.model.user.SignupRequest;
import com.web.curation.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin
@RestController
public class AccountController {

    @Autowired
    UserDao userDao;

    @PostMapping("/account/login")
    @ApiOperation(value = "로그인")
    public Object login(@RequestBody(required = true) final Map<String,Object> request) {

        String email = (String)request.get("email");
        int password = ((String)request.get("password")).hashCode();
        //String password = (String)request.get("password");

        final Optional<User> userOpt = userDao.findUserByEmailAndPassword(email, password);

        ResponseEntity response = null;

        final BasicResponse result = new BasicResponse();
        if (userOpt.isPresent()) { // id/pw 다 맞을 때
            result.status = true;
            result.data = "success";
            result.object = userOpt.get();                     

        } else { // id/pw 둘 중 하나라도 안 맞으면
            Optional<User> emailOpt = userDao.findUserByEmail(email);
            result.status = false;            

            if(emailOpt.isPresent()) { // pw 가 틀린 경우
                result.data = "비밀번호가 일치하지 않습니다.";
            } else { // id 자체가 존재하지 않는 경우
                result.data = "등록되지 않은 E-mail 입니다.";
            }
            
        }
        response = new ResponseEntity<>(result, HttpStatus.OK); 

        return response;
    }

    @PostMapping("/account/signup")
    @ApiOperation(value = "가입하기")
    public Object signup(@Valid @RequestBody final SignupRequest request) {
        // 이메일, 닉네임 중복처리 필수
        // 회원가입단을 생성해 보세요

        String email = request.getEmail();
        int password = ((String)request.getPassword()).hashCode();
        String nickname = request.getNickname();
        String info = request.getInfo();
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setInfo(info);
        userDao.save(user);
        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";
        result.object = userDao.findUserByEmail(email).get(); 
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/account/checkEmail")
    @ApiOperation(value = "이메일 체크")
    public Object checkEmail(@RequestBody(required = true) final Map<String, Object> request) {

        String email = (String)request.get("email");
        final Optional<User> user = userDao.findUserByEmail(email);

        final BasicResponse result = new BasicResponse();

        if (user.isPresent()) { // user 있으면 => 가입 x

            result.status = false;
            result.data = "이미 가입 된 이메일 입니다";
        } else {

            result.status = true;
            result.data = "사용 가능한 이메일 입니다";

            // 인증 이메일 보내는 작업
            try {
                Map<String, String> response = new HashMap<>();
                response.put("code", sendMail(email));
                result.object = response;
                
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    //이메일 설정 함수
    @Autowired
    JavaMailSender javaMailSender;
    
    public String sendMail(String email) throws MessagingException {
        //인증 번호 랜덤 생성
        StringBuffer ranString = new StringBuffer();
        Random ran = new Random();
        int len = ran.nextInt(5);
        for(int i=0;i<len+10;i++){
            int Index = ran.nextInt(3);
            switch(Index){
                case 0:
                    //소문자 랜덤
                    ranString.append((char) ((int) (ran.nextInt(26)) + 97));
                    break;
                case 1:
                    //대문자 랜덤
                    ranString.append((char) ((int) (ran.nextInt(26)) + 65));
                    break; 
                case 2:
                    //숫자 랜덤
                    ranString.append((ran.nextInt(10)));
                    break;
            }

        }
        
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setSubject("TWL 인증 메일입니다.");
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setText("안녕하세요. 인증 번호는 "+ranString+"입니다.");
        message.setSentDate(new Date());
        javaMailSender.send(message);

        return ranString.toString();
    }
    



    @PostMapping("/account/checkNickname")
    @ApiOperation(value = "닉네임 중복 체크")
    public Object checkNickname(@RequestBody(required = true) final Map<String, Object> request) {
        final Optional<User> user = userDao.findUserByNickname((String)request.get("nickname"));

        final BasicResponse result = new BasicResponse();

        if(user.isPresent()){ // user 있으면 => 가입 x
            result.status = false;
            result.data = "이미 사용중인 닉네임 입니다";
        } else{
            result.status = true;
            result.data = "사용 가능한 닉네임 입니다";
        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @PostMapping("/account/findPwd")
    @ApiOperation(value = "비밀번호 찾기 - 이메일 입력 후 인증 이메일 발송")
    public Object findPwd(@RequestBody(required = true) final Map<String,Object> email) {

        final Optional<User> user = userDao.findUserByEmail((String)email.get("email"));

        final BasicResponse result = new BasicResponse();

        if(user.isPresent()){ // user 있으면 인증 이메일 보내기
            result.status = true;
            result.data = "가입하신 이메일로 인증번호를 보내드렸습니다";
            
            // 이메일 발송 코드
            try {
                Map<String, String> response = new HashMap<>();
                response.put("code", sendMail((String)email.get("email")));
                result.object = response;
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        } else{
            result.status = false;
            result.data = "등록되지 않은 이메일 입니다";
        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    
    @PostMapping("/account/changePwd")
    @ApiOperation(value = "비밀번호 변경")
    public Object changePwd(@RequestBody(required = true) final Map<String,Object> request){

        final Optional<User> userOpt = userDao.findUserByEmail((String)request.get("email"));

        final BasicResponse result = new BasicResponse();
        
        result.status = true;
        result.data = "비밀번호 변경 성공! 다시 로그인 해주세요";
        
        User user = userOpt.get();
        user.setPassword(((String)request.get("password")).hashCode());

        userDao.save(user);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}