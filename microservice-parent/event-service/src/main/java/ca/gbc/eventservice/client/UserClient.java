package ca.gbc.eventservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(value="user", url ="${user.service.url}")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value="/users/{id}/role")
    String checkUserRole(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.GET, value="/users/exist/{id}")
    Boolean isUserExist(@PathVariable("id") Long id) ;


}
