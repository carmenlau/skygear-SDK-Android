/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear_example;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.Query;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordQueryResponseHandler;

public class UserQueryActivity extends AppCompatActivity {

    private EditText userEmailEditText;
    private TextView display;

    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_query);

        this.userEmailEditText = (EditText) findViewById(R.id.user_email_edit_text);
        this.display = (TextView) findViewById(R.id.query_display);

        this.skygear = Container.defaultContainer(this);
    }

    private void display(Record[] records) {
        String displayText;
        if (records == null || records.length == 0) {
            displayText = "No records found";
        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append(String.format("Got %d records\n\n", records.length));
            try {
                for (int idx = 0; idx < records.length; idx++) {
                    buffer.append(String.format("Record[%d]:\n", idx))
                            .append(records[idx].toJson().toString(2))
                            .append("\n\n");
                }
                displayText = buffer.toString();
            } catch (JSONException e) {
                displayText = "Invalid JSON format for user records";
            }
        }
        this.display.setText(displayText);
    }

    @SuppressLint("DefaultLocale")
    public void doQuery(View view) {
        String email = this.userEmailEditText.getText().toString();
        if (email.length() == 0) {
            return;
        }

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Querying user...");
        loading.show();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Query failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();


        Query query = new Query("user");
        query.equalTo("email", email);
        skygear.getPublicDatabase().query(query, new RecordQueryResponseHandler() {

            @Override
            public void onQuerySuccess(Record[] records) {
                loading.dismiss();
                display(records);
            }

            @Override
            public void onQueryError(Error error) {
                failDialog.setMessage(
                        String.format("Fail with reason:\n%s", error.getDetailMessage())
                );
            }
        });
    }
}
