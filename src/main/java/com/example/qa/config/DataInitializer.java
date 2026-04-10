package com.example.qa.config;

import com.example.qa.entity.User;
import com.example.qa.entity.Question;
import com.example.qa.entity.Answer;
import com.example.qa.entity.Tag;
import com.example.qa.mapper.UserMapper;
import com.example.qa.mapper.QuestionMapper;
import com.example.qa.mapper.AnswerMapper;
import com.example.qa.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

// 数据初始化组件
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private AnswerMapper answerMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeTags();
        initializeQuestions();
        initializeAnswers();
    }
    
    private void initializeUsers() {
        if (userMapper.selectCount(null) == 0) {
            List<User> users = Arrays.asList(
                User.builder()
                    .username("admin")
                    .password("admin123")
                    .email("admin@example.com")
                    .role("ADMIN")
                    .points(1000)
                    .gender("MALE")
                    .age(30)
                    .bio("系统管理员")
                    .createdAt(new Date())
                    .build(),
                User.builder()
                    .username("user1")
                    .password("user123")
                    .email("user1@example.com")
                    .role("USER")
                    .points(100)
                    .gender("MALE")
                    .age(25)
                    .bio("Java开发工程师")
                    .createdAt(new Date())
                    .build(),
                User.builder()
                    .username("user2")
                    .password("user123")
                    .email("user2@example.com")
                    .role("USER")
                    .points(50)
                    .gender("FEMALE")
                    .age(28)
                    .bio("Python开发工程师")
                    .createdAt(new Date())
                    .build()
            );
            for (User user : users) {
                userMapper.insert(user);
            }
        }
    }
    
    private void initializeTags() {
        if (tagMapper.selectCount(null) == 0) {
            List<Tag> tags = Arrays.asList(
                Tag.builder()
                    .name("Java")
                    .description("Java编程语言相关")
                    .createdAt(new Date())
                    .build(),
                Tag.builder()
                    .name("Python")
                    .description("Python编程语言相关")
                    .createdAt(new Date())
                    .build(),
                Tag.builder()
                    .name("Spring")
                    .description("Spring框架相关")
                    .createdAt(new Date())
                    .build(),
                Tag.builder()
                    .name("MySQL")
                    .description("MySQL数据库相关")
                    .createdAt(new Date())
                    .build(),
                Tag.builder()
                    .name("前端开发")
                    .description("HTML、CSS、JavaScript等前端技术")
                    .createdAt(new Date())
                    .build(),
                Tag.builder()
                    .name("算法")
                    .description("数据结构与算法相关")
                    .createdAt(new Date())
                    .build()
            );
            for (Tag tag : tags) {
                tagMapper.insert(tag);
            }
        }
    }
    
    private void initializeQuestions() {
        if (questionMapper.selectCount(null) == 0) {
            List<User> users = userMapper.selectList(null);
            List<Tag> tags = tagMapper.selectList(null);
            
            if (users.size() >= 2 && tags.size() >= 2) {
                List<Question> questions = Arrays.asList(
                    Question.builder()
                        .title("如何在Spring Boot中使用MyBatis？")
                        .content("我想在Spring Boot项目中集成MyBatis，应该如何配置？有没有详细的步骤？")
                        .userId(users.get(1).getId())
                        .viewCount(100)
                        .answerCount(2)
                        .createdAt(new Date())
                        .build(),
                    Question.builder()
                        .title("Python中如何处理JSON数据？")
                        .content("在Python中，我需要解析和生成JSON数据，应该使用哪个库？json还是simplejson？")
                        .userId(users.get(2).getId())
                        .viewCount(80)
                        .answerCount(1)
                        .createdAt(new Date())
                        .build(),
                    Question.builder()
                        .title("MySQL索引优化有什么技巧？")
                        .content("我的MySQL查询很慢，应该如何优化索引？有哪些最佳实践？")
                        .userId(users.get(1).getId())
                        .viewCount(150)
                        .answerCount(3)
                        .createdAt(new Date())
                        .build(),
                    Question.builder()
                        .title("JavaScript闭包是什么？")
                        .content("我经常听到JavaScript闭包的概念，但不太理解，能否用简单的例子说明？")
                        .userId(users.get(2).getId())
                        .viewCount(200)
                        .answerCount(4)
                        .createdAt(new Date())
                        .build(),
                    Question.builder()
                        .title("如何实现快速排序算法？")
                        .content("我需要实现快速排序算法，能否提供Python和Java两种版本的代码？")
                        .userId(users.get(1).getId())
                        .viewCount(120)
                        .answerCount(2)
                        .createdAt(new Date())
                        .build()
                );
                for (Question question : questions) {
                    questionMapper.insert(question);
                }
            }
        }
    }
    
    private void initializeAnswers() {
        if (answerMapper.selectCount(null) == 0) {
            List<Question> questions = questionMapper.selectList(null);
            List<User> users = userMapper.selectList(null);
            
            if (questions.size() >= 2 && users.size() >= 2) {
                List<Answer> answers = Arrays.asList(
                    Answer.builder()
                        .questionId(questions.get(0).getId())
                        .content("在Spring Boot中使用MyBatis很简单：\n1. 添加mybatis-spring-boot-starter依赖\n2. 配置数据源\n3. 创建Mapper接口\n4. 使用@Mapper注解\n具体配置可以参考官方文档。")
                        .userId(users.get(0).getId())
                        .createdAt(new Date())
                        .build(),
                    Answer.builder()
                        .questionId(questions.get(0).getId())
                        .content("我推荐使用MyBatis Plus，它提供了很多便捷的方法，可以大大提高开发效率。")
                        .userId(users.get(1).getId())
                        .createdAt(new Date())
                        .build(),
                    Answer.builder()
                        .questionId(questions.get(1).getId())
                        .content("Python中处理JSON推荐使用内置的json库，它是标准库的一部分，性能也很好。simplejson是第三方库，在Python 2中更常用。")
                        .userId(users.get(0).getId())
                        .createdAt(new Date())
                        .build()
                );
                for (Answer answer : answers) {
                    answerMapper.insert(answer);
                }
            }
        }
    }
}
