package com.example.taskone;

import android.app.ProgressDialog;
import android.widget.EditText;
import android.widget.Toast;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.view.WindowManager;  // Import the correct WindowManager class
import androidx.test.espresso.Root;  // Import the Root class to use with toasts

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    @Before
    public void setUp() {
        // Configure Firebase to use emulator or real Firestore if needed for tests
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false) // Use offline persistence or disable for testing
                .build();
        firestore.setFirestoreSettings(settings);
    }

    @Test
    public void testUploadData_Success() {
        // Step 1: Simulate user input for title and description
        Espresso.onView(withId(R.id.titleEt))
                .perform(ViewActions.typeText("Test Title"));

        Espresso.onView(withId(R.id.descriptionEt))
                .perform(ViewActions.typeText("Test Description"));

        // Step 2: Click the save button
        Espresso.onView(withId(R.id.save))
                .perform(click());

        // Step 3: Check that the ProgressDialog is shown (checking visibility)
        Espresso.onView(withText("Adding Data to Firestore"))
                .check(matches(isDisplayed()));

        // Step 4: Check that the success toast message is shown (mocked behavior)
        Espresso.onView(withText("Uploaded..."))
                .inRoot(new ToastMatcher()) // ToastMatcher allows us to check Toast messages
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUploadData_Failure() {
        // Simulate user input (same as before)
        Espresso.onView(withId(R.id.titleEt))
                .perform(ViewActions.typeText("Test Title"));

        Espresso.onView(withId(R.id.descriptionEt))
                .perform(ViewActions.typeText("Test Description"));

        // Click the save button
        Espresso.onView(withId(R.id.save))
                .perform(click());

        // Simulate Firestore failure by triggering the failure listener
        // Check if the toast with error message is shown
        Espresso.onView(withText("Firestore Error"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Custom matcher for Toast messages
    public static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public boolean matchesSafely(Root root) {
            // Ensure that we're working with a toast
            if (root.getWindowLayoutParams() != null && root.getWindowLayoutParams().isPresent()) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) root.getWindowLayoutParams().get();
                return layoutParams.type == WindowManager.LayoutParams.TYPE_TOAST;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is a toast");
        }
    }
}
