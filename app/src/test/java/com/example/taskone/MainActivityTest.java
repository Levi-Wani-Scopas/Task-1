package com.example.taskone;

import static org.mockito.Mockito.*;

import android.app.ProgressDialog;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivityTest {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockCollection;

    @Mock
    private DocumentReference mockDocument;

    @Mock
    private ProgressDialog mockProgressDialog;

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mainActivity = new MainActivity();
        mainActivity.db = mockFirestore;
        mainActivity.pd = mockProgressDialog;

        when(mockFirestore.collection("Documents")).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);
    }

    @Test
    public void testUploadData_Success() {
        // Mock successful Firestore upload
        Task<Void> mockTask = mock(Task.class);
        when(mockDocument.set(any(Map.class))).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);

        // Call uploadData()
        mainActivity.uploadData("Test Title", "Test Description");

        // Capture the data sent to Firestore
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDocument).set(captor.capture());

        // Verify data correctness
        Map<String, Object> capturedData = captor.getValue();
        assert capturedData.get("tite").equals("Test Title");
        assert capturedData.get("descrtiption").equals("Test Description");

        // Verify progress dialog is shown and dismissed
        verify(mockProgressDialog).setTitle("Adding Data to Firestore");
        verify(mockProgressDialog).show();
        verify(mockProgressDialog).dismiss();
    }

    @Test
    public void testUploadData_Failure() {
        // Mock failure
        Task<Void> mockTask = mock(Task.class);
        when(mockDocument.set(any(Map.class))).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        doAnswer(invocation -> {
            OnFailureListener failureListener = invocation.getArgument(0);
            failureListener.onFailure(new Exception("Firestore Error"));
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        // Call uploadData()
        mainActivity.uploadData("Test Title", "Test Description");

        // Verify progress dialog dismissal and error handling
        verify(mockProgressDialog).dismiss();
    }
}

