package cz.kerslager.android.mzda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editTextHrubaMzda;
    TextView textViewCistaMzda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextHrubaMzda = (EditText) findViewById(R.id.editTextHrubaMzda);
        textViewCistaMzda = (TextView) findViewById(R.id.textViewCistaMzda);
    }

    public void onButtonKlik(View view) {
        // https://www.vypocet.cz/popis-vypoctu-ciste-mzdy
        int HrubaMzda=0;
        try {
            HrubaMzda = Integer.parseInt(editTextHrubaMzda.getText().toString());
        } catch (NumberFormatException e) {
            showError("Chybný vstup!");
        }
        Double zdravotni = HrubaMzda * 0.09;
        Double socialni = HrubaMzda * 0.25;
        int superhruba = HrubaMzda + zdravotni.intValue()+socialni.intValue();
        // zaokrouhlit superhrubou mzdu na stovky nahoru
        // superhruba je celé číslo, výsledek je zaokrouhlen sám
        superhruba = ((superhruba+100)/100)*100;
        // zaloha zaokrouhlena na koruny
        int zalohanadanzprijmu = (int) Math.round(superhruba*0.15);
        zalohanadanzprijmu -= 2070; // sleva na poplatníka
        Double zamsocialni = HrubaMzda * 0.065;
        Double zamzdravotni = HrubaMzda * 0.045;
        int CistaMzda = HrubaMzda - zalohanadanzprijmu
                -zamsocialni.intValue() - zamzdravotni.intValue();
        textViewCistaMzda.setText(Integer.toString(CistaMzda));
        // při 11000 má vyjít 9640
    }
    private void showError(String zprava) {
        Toast myToast = Toast.makeText(
                getApplication(), zprava,
                Toast.LENGTH_LONG
        );
        myToast.show();
    }
}
