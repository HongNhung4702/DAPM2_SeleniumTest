package com.footballbooking.suites;

import com.footballbooking.tests.BookingTest;
import com.footballbooking.tests.CancelBookingTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BookingTest.class,
        CancelBookingTest.class
})
public class BookingSuite {
}
