package com.footballbooking.suites;

import com.footballbooking.tests.AdminBookingTest;
import com.footballbooking.tests.AdminStadiumTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AdminBookingTest.class,
        AdminStadiumTest.class
})
public class AdminSuite {
}
