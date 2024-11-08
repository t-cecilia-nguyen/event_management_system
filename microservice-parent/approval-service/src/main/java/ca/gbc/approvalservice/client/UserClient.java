package ca.gbc.approvalservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "user", url="${user.service.url}")
public interface UserClient {

    @RequestMapping(method= RequestMethod.GET, value ="/users/{id}/usertype")
    String checkUserType(@PathVariable("id") String id);
}
