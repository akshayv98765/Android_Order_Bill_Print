package com.example.akshayassignment1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout txtCustName, txtCustEmail, txtCustPhoneNo, txtLayoutRegion;
    private TextInputEditText custName, custEmail, custPhoneNo, salesDate;
    private RadioGroup rgBeverageChoice;
    private RadioButton rbTea, rbCoffee;
    private CheckBox cbMilk, cbSugar;
    private Spinner spnBeverageSize, spnRegions;
    private AutoCompleteTextView acRegion;
    private Button btnPrint;
    private TextView textView;

    // Array of available regions
    private static final String[] REGIONS = {"Waterloo", "London", "Milton", "Mississauga"};

    // Stores corresponding to each region
    private static final String[][] STORES = {
            {"65 University Ave E", "415 King St", "585 Weber St"},
            {"616 Wharncliffe Rd", "1885 Huron St", "670 Wonderland Road"},
            {"900 Steeles Ave", "80 Market Dr", "820 Main St"},
            {"144 Dundas St", "3411 Mavis Rd", "30 Eglinton Ave", "6075 Creditview Rd"}
    };

    // Available beverage sizes
    private static final String[] BEVERAGE_SIZES = {"Small", "Medium", "Large"};

    // Price for tea based on size
    private static final double[] PRICE_TEA = {1.5, 2.5, 3.25};

    // Price for coffee based on size
    private static final double[] PRICE_COFFEE = {1.75, 2.75, 3.75};

    // Price for additional items (milk and sugar)
    private static final double[] PRICE_ADDITIONALS = {1.25, 1.0};

    // Price for flavorings
    private static final double[] PRICE_FLAVORS = {0.0, 0.5, 0.75};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize references to TextInputLayouts
        txtCustName = findViewById(R.id.txtCustName);
        txtCustEmail = findViewById(R.id.txtCustEmail);
        txtCustPhoneNo = findViewById(R.id.txtCustPhoneNo);
        txtLayoutRegion = findViewById(R.id.txtLayoutRegion);

        // Initialize references to TextInputEditTexts
        custName = findViewById(R.id.custName);
        custEmail = findViewById(R.id.custEmail);
        custPhoneNo = findViewById(R.id.custPhoneNo);

        // Initialize references to RadioGroup and RadioButtons
        rgBeverageChoice = findViewById(R.id.rgBeverageChoice);
        rbTea = findViewById(R.id.rbTea);
        rbCoffee = findViewById(R.id.rbCoffee);

        // Initialize references to CheckBoxes
        cbMilk = findViewById(R.id.cbMilk);
        cbSugar = findViewById(R.id.cbSugar);

        // Initialize reference to Spinner for beverage size selection
        spnBeverageSize = findViewById(R.id.spnBeverageSize);

        // Initialize reference to AutoCompleteTextView for region selection
        acRegion = findViewById(R.id.acRegion);

        // Initialize reference to TextView
        textView = findViewById(R.id.textView);

        // Initialize reference to Spinner for store selection
        spnRegions = findViewById(R.id.spnRegions);

        // Initialize reference to DatePickerEditText for sales date selection
        salesDate = findViewById(R.id.datePickerEditText);
        salesDate.setFocusable(false);
        salesDate.setClickable(true);

        // Initialize reference to Button for printing
        btnPrint = findViewById(R.id.btnPrint);

        // Set up ArrayAdapter for region AutoCompleteTextView
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, REGIONS);
        acRegion.setAdapter(regionAdapter);

        // Set up ArrayAdapter for store Spinner
        ArrayAdapter<CharSequence> storeAdapter = ArrayAdapter.createFromResource(this,
                R.array.waterlooStores, android.R.layout.simple_spinner_dropdown_item);
        spnRegions.setAdapter(storeAdapter);

        // Set up item click listener for AutoCompleteTextView
        acRegion.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRegion = acRegion.getText().toString().trim();
            int regionIndex = -1;

            // Find the index of the selected region in the REGIONS array
            for (int i = 0; i < REGIONS.length; i++) {
                if (REGIONS[i].equals(selectedRegion)) {
                    regionIndex = i;
                    break;
                }
            }

            if (regionIndex >= 0) {
                // Retrieve the corresponding stores for the selected region
                String[] stores = STORES[regionIndex];

                // Update the store Spinner with the new stores
                ArrayAdapter<String> storeAdapter1 = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, stores);
                spnRegions.setAdapter(storeAdapter1);
            }
        });

        // Set up ArrayAdapter for beverage size Spinner
        spnBeverageSize.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, BEVERAGE_SIZES));

        // Set up listener for beverage choice RadioGroup
        rgBeverageChoice.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTea) {
                // Update flavoring options for tea
                updateFlavoringRadioButtons(new String[]{"None", "Ginger", "Honey"});
            } else if (checkedId == R.id.rbCoffee) {
                // Update flavoring options for coffee
                updateFlavoringRadioButtons(new String[]{"None", "Pumpkin Spice", "Caramel"});
            }
        });

        // Set click listener for sales date selection
        salesDate.setOnClickListener(v -> showDatePicker());

        // Set click listener for print button
        btnPrint.setOnClickListener(v -> calculateBill());

    }

    private void updateFlavoringRadioButtons(String[] flavors) {
        // Get the reference to the flavoring RadioGroup
        RadioGroup flavoringGroup = findViewById(R.id.addingFlavourGroup);

        // Remove all views from the RadioGroup to clear previous options
        flavoringGroup.removeAllViews();

        // Iterate through the flavors array to create radio buttons
        for (String flavor : flavors) {
            // Create a new RadioButton
            RadioButton radioButton = new RadioButton(this);

            // Set the text for the radio button to the flavor
            radioButton.setText(flavor);

            // Add the radio button to the flavoring RadioGroup
            flavoringGroup.addView(radioButton);
        }
    }


    private void showDatePicker() {
        // Create a DatePickerDialog.OnDateSetListener to handle the date selection
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Create a Calendar instance for the selected date
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create a Calendar instance for the current date
                Calendar currentDate = Calendar.getInstance();

                // Compare the selected date with the current date
                if (selectedDate.compareTo(currentDate) <= 0) {
                    // The selected date is less than or equal to the current date
                    // Proceed with further actions, such as setting the selected date in the EditText

                    // Format the selected date using SimpleDateFormat
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    // Set the formatted date in the salesDate EditText
                    salesDate.setText(formattedDate);
                } else {
                    // The selected date is greater than the current date
                    // Show an error message or take appropriate action
                    Toast.makeText(MainActivity.this, "Invalid date. Please select a date less than or equal to the current date.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Create a Calendar instance for the current date
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog with the current date and the dateSetListener, and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, dateSetListener, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }


    private void calculateBill() {
        // Retrieve input values from the EditText and other UI elements
        String name = custName.getText().toString().trim();
        String email = custEmail.getText().toString().trim();
        String cellNo = custPhoneNo.getText().toString().trim();
        String region = acRegion.getText().toString().trim();
        String store = (String) spnRegions.getSelectedItem();

        // Validate the required fields
        if (name.isEmpty() || email.isEmpty() || cellNo.isEmpty() || region.isEmpty()) {
            Toast.makeText(this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate the email format
        if (!isValidEmail(email)) {
            txtCustEmail.setError("Invalid email format");
            return;
        }

        // Calculate the total bill
        double totalBill = calculateTotalBill();

        // Format the bill amount
        DecimalFormat decimalFormat = new DecimalFormat("$0.00");
        String formattedBill = decimalFormat.format(totalBill);

        System.out.println(formattedBill);

        // Retrieve the selected beverage type, additionals, flavoring, and size
        String beverageType = rbTea.isChecked() ? "Tea" : "Coffee";
        String additionals = (cbMilk.isChecked() ? "Milk " : "") + (cbSugar.isChecked() ? "Sugar" : "");
        String flavoring = getSelectedFlavoring();
        String size = (String) spnBeverageSize.getSelectedItem();

        // Prepare the details string
        String details = "Name: " + name + "\n" +
                "Phone: " + cellNo + "\n" +
                "Type of Beverage: " + beverageType + "\n" +
                "Additionals: " + additionals + "\n" +
                "Flavoring: " + flavoring + "\n" +
                "Size: " + size + "\n" +
                "Region: " + region + "\n" +
                "Store: " + store + "\n" +
                "Total Bill: " + formattedBill;

        // Create and customize the Snackbar
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarText.setVisibility(View.INVISIBLE); // Hide the default text view

        LayoutInflater inflater = LayoutInflater.from(this);
        View customSnackbarView = inflater.inflate(R.layout.custom_snackbar, null);
        TextView customSnackbarText = customSnackbarView.findViewById(R.id.snackbar_text);
        customSnackbarText.setText(details);

        snackbarView.setBackgroundColor(getResources().getColor(R.color.black));
        snackbarView.setPadding(0, 0, 0, 0);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarView;
        snackbarLayout.addView(customSnackbarView, 0);

        // Show the custom Snackbar
        snackbar.show();
    }

    private double calculateTotalBill() {
        // Get the selected index of the beverage size spinner
        int sizeIndex = spnBeverageSize.getSelectedItemPosition();
        // Check if milk checkbox is checked
        boolean hasMilk = cbMilk.isChecked();
        // Check if sugar checkbox is checked
        boolean hasSugar = cbSugar.isChecked();
        // Get the selected index of the flavoring radio buttons
        int flavorIndex = getSelectedFlavoringIndex();

        // Check if the selected indices are within valid ranges
        if (sizeIndex >= 0 && (sizeIndex < PRICE_TEA.length || sizeIndex < PRICE_COFFEE.length)
                && flavorIndex >= 0 && flavorIndex < PRICE_FLAVORS.length) {

            double basePrice = 0.0;
            double additionalPrice = 0.0;

            // Calculate the base price based on the selected beverage type (tea or coffee)
            if (rbTea.isChecked()) {
                basePrice += PRICE_TEA[sizeIndex];
            }

            if (rbCoffee.isChecked()) {
                basePrice += PRICE_COFFEE[sizeIndex];
            }

            // Calculate the additional price based on selected additionals (milk and sugar)
            if (hasMilk) {
                additionalPrice += PRICE_ADDITIONALS[0]; // Milk price
            }

            if (hasSugar) {
                additionalPrice += PRICE_ADDITIONALS[1]; // Sugar price
            }

            // Add the flavoring price
            additionalPrice += PRICE_FLAVORS[flavorIndex];

            // Calculate the total price including tax (13%)
            double totalPrice = basePrice + additionalPrice;
            return totalPrice * 1.13;
        } else {
            // Handle invalid indices gracefully
            return 0.0; // or any appropriate value in case of an error
        }
    }

    private String getSelectedFlavoring() {
        // Get the radio group for flavoring options
        RadioGroup flavoringGroup = findViewById(R.id.addingFlavourGroup);
        // Get the ID of the checked radio button
        int checkedId = flavoringGroup.getCheckedRadioButtonId();
        // Find the radio button based on the ID
        RadioButton radioButton = findViewById(checkedId);
        // Return the text of the selected flavoring option
        return radioButton != null ? radioButton.getText().toString() : "";
    }

    private int getSelectedFlavoringIndex() {
        // Get the radio group for flavoring options
        RadioGroup flavoringGroup = findViewById(R.id.addingFlavourGroup);
        // Get the ID of the checked radio button
        int checkedId = flavoringGroup.getCheckedRadioButtonId();
        // Find the radio button based on the ID
        View radioButton = flavoringGroup.findViewById(checkedId);
        // Return the index of the selected flavoring option
        return flavoringGroup.indexOfChild(radioButton);
    }

    private boolean isValidEmail(String email) {
        // Validate the email using a regular expression
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}