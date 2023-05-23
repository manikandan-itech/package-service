package com.mobiquity.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.mobiquity.app.RestControllerExceptionHandlerConfiguration;



@ContextConfiguration(classes = PackageControllerIT.TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PackageControllerIT {
	
	
	@Autowired
	MockMvc mockMvc;
	
	@WithMockUser
	@Test
    void testPackageCollecctor() throws Exception {
        
		// Mock Request
		MockMultipartFile jsonFile = new MockMultipartFile("package.txt", "", "application/txt", "120: (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€102) (5,30.18,€9) (6,46.34,€48)".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/packageFile")
                .file("file", jsonFile.getBytes())
                .characterEncoding("UTF-8").with(csrf()))
        .andExpect(status().isOk());

        
    }
	
	@Test
    void when_user_is_authorized_to_execute_then_expect_unauthorized_error() throws Exception {
        
		// Mock Request
		MockMultipartFile jsonFile = new MockMultipartFile("package.txt", "", "application/txt", "81: (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€102) (5,30.18,€9) (6,46.34,€48)".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/packageFile")
                .file("file", jsonFile.getBytes())
                .characterEncoding("UTF-8").with(csrf()))
        .andExpect(status().isUnauthorized());

        
    }
	
	@WithMockUser
	@Test
    void when_line_is_incorrect_in_file_then_except_bad_request_error() throws Exception {
        
		// Mock Request
		MockMultipartFile jsonFile = new MockMultipartFile("package.txt", "", "application/txt", "81w: (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€102) (5,30.18,€9) (6,46.34,€48)".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/packageFile")
                .file("file", jsonFile.getBytes())
                .characterEncoding("UTF-8").with(csrf()))
        .andExpect(status().isBadRequest());

        
    }
	
	
	@WithMockUser
	@Test
    void testPackageCollecctorFromPath() throws Exception {
        
		mockMvc.perform(MockMvcRequestBuilders.post("/api/packageFilePath") //
                .headers(new HttpHeaders()) //
                .param("filePath", "src/test/resources/package.txt").with(csrf())) //
              .andExpect(status().isOk()); 
    }
	
	@WithMockUser
	@Test
    void when_file_is_not_found_then_except_bad_request_error() throws Exception {
        
		mockMvc.perform(MockMvcRequestBuilders.post("/api/packageFilePath") //
                .headers(new HttpHeaders()) //
                .param("filePath", "/package2.txt").with(csrf())) //
              .andExpect(status().isInternalServerError()); 
    }
	
	
	@Configuration
    @Import({ PackageController.class, RestControllerExceptionHandlerConfiguration.class })
    static class TestConfig {
    }

}
