package com.cholera.eagleeye.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cholera.eagleeye.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Properties;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.ApiException;
import android.os.AsyncTask;



public class AlertsFragment extends Fragment {

    private static final String TAG = "EmailFragment";
    private static final String APPLICATION_NAME = "Eagleee";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT;

    private static final java.util.Collection<String> SCOPES = Collections.singleton(GmailScopes.GMAIL_SEND);
    private GoogleSignInClient googleSignInClient;

    private static final int REQUEST_CODE_SIGN_IN = 1;

    private EditText alertTitle, alertDescription, alertTime;
    private TextView alertStatus;
    private Spinner spinnerRisk, spinnerPriority, spinnerLocation, spinnerEmail;
    private Button btnSendAlert;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        alertTitle = view.findViewById(R.id.alert_title);
        alertDescription = view.findViewById(R.id.alert_description);
        alertTime = view.findViewById(R.id.alert_time);
        alertStatus = view.findViewById(R.id.alert_status);
        spinnerRisk = view.findViewById(R.id.spinner_risk);
        spinnerPriority = view.findViewById(R.id.spinner_priority);
        spinnerLocation = view.findViewById(R.id.spinner_location);
        spinnerEmail = view.findViewById(R.id.spinner_email);
        btnSendAlert = view.findViewById(R.id.btn_send_alert);

        // Set up the spinners with example data
        ArrayAdapter<CharSequence> riskAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.risk_levels, android.R.layout.simple_spinner_item);
        riskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRisk.setAdapter(riskAdapter);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.locations_malawi, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        ArrayAdapter<CharSequence> emailAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.email_addresses, android.R.layout.simple_spinner_item);
        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmail.setAdapter(emailAdapter);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(GmailScopes.GMAIL_SEND))
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        btnSendAlert.setOnClickListener(v -> {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
            sendEmail(account);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                sendEmail(account); // Pass the account object to sendEmail
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In failed", e);
                alertStatus.setText("Sign-In failed");
            }
        }
    }

    private void sendEmail(GoogleSignInAccount account) {
        if (account == null) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        } else {
            new SendEmailTask().execute(account);
        }
    }

    private class SendEmailTask extends AsyncTask<GoogleSignInAccount, Void, Void> {
        @Override
        protected Void doInBackground(GoogleSignInAccount... accounts) {
            GoogleSignInAccount account = accounts[0];
            try {
                MimeMessage mimeMessage = createMessageWithEmail(
                        alertTitle.getText().toString(),
                        alertDescription.getText().toString(),
                        alertTime.getText().toString(),
                        spinnerRisk.getSelectedItem().toString(),
                        spinnerPriority.getSelectedItem().toString(),
                        spinnerLocation.getSelectedItem().toString(),
                        spinnerEmail.getSelectedItem().toString()
                );
                sendMessage(getGmailService(account), mimeMessage);
                requireActivity().runOnUiThread(() -> alertStatus.setText("Alert Status: Sent"));
                Log.e(TAG, " send email");
            } catch (Exception e) {
                Log.e(TAG, "Failed to send email", e);
                requireActivity().runOnUiThread(() -> alertStatus.setText("Alert Status: Failed"));
            }
            return null;
        }
    }

    private MimeMessage createMessageWithEmail(String title, String description, String time, String risk, String priority, String location, String email) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(getSession());
        mimeMessage.setFrom(new InternetAddress("h.goliyo@alustudent.com"));
        mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));
        mimeMessage.setSubject(title);
        mimeMessage.setText("Description: " + description + "\nTime: " + time + "\nRisk: " + risk + "\nPriority: " + priority + "\nLocation: " + location);
        return mimeMessage;
    }

    private javax.mail.Session getSession() {
        return javax.mail.Session.getInstance(getProperties());
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }

    private Gmail getGmailService(GoogleSignInAccount account) throws GeneralSecurityException, IOException {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                requireContext(), SCOPES);
        credential.setSelectedAccount(account.getAccount());
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void sendMessage(Gmail service, MimeMessage email) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.URL_SAFE | android.util.Base64.NO_WRAP);

        Message message = new Message().setRaw(encodedEmail);
        try {
            service.users().messages().send("me", message).execute();
        } catch (GoogleJsonResponseException e) {
            Log.e(TAG, "GoogleJsonResponseException: " + e.getDetails().getMessage());
            throw e;
        }
    }
}

