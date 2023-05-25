package edu.ucsb.cs156.gauchoride.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.gauchoride.models.CurrentUser;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;


@Api(description="Current User Information")
@RequestMapping("/api/currentUser")
@RestController
public class UserInfoController extends ApiController {

  @Autowired
  UserRepository userRepository;
 
  @ApiOperation(value = "Get information about current user")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("")
  public CurrentUser getCurrentUser() {
    return super.getCurrentUser();
  }
}
