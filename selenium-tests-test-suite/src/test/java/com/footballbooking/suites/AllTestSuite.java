package com.footballbooking.suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AuthenticationSuite.class,
        BookingSuite.class,
        AdminSuite.class
})
public class AllTestSuite {
}
