package com.footballbooking.suites;

import com.footballbooking.tests.LoginTest;
import com.footballbooking.tests.RegisterTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        LoginTest.class,
        RegisterTest.class
})
public class AuthenticationSuite {
}
