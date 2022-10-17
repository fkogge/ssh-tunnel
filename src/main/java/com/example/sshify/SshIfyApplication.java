package com.example.sshify;

import com.jcraft.jsch.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;


@SpringBootApplication
@RestController
public class SshIfyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SshIfyApplication.class, args);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return new ResponseEntity<>("Hello, " + name, HttpStatus.OK);
    }

    @PostMapping("/session")
    public ResponseEntity<String> session() {
        Session session = null;
        StringBuilder output = new StringBuilder();
        ChannelExec channel = null;
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts("");
            String host = "";
            String user = "";
            String password = "";
            int port = 22;

            //java.util.Properties config = new java.util.Properties();
            //config.put("StrictHostKeyChecking", "no");

            session = jsch.getSession(user, host, port);
            //session.setConfig(config);
            session.setPassword(password);

            session.connect();
            System.out.println("ssh - connected");

            String[] commands = {"ls"};

            for (String command : commands) {
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                channel.setOutputStream(outputStream);
                channel.setErrStream(errorStream);

                channel.connect();
                while (channel.isConnected()) {
                    Thread.sleep(100);
                }
                output.append(outputStream);
                System.out.println(outputStream);
                String errorResponse = errorStream.toString();
                channel.disconnect();
            }






        } catch (Exception e) {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            if (session != null) {
                System.out.println("session did connect");
                session.disconnect();
            }
            if (channel != null) {
                System.out.println("channel did connect");
                channel.disconnect();
            }
        }

        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }
}
