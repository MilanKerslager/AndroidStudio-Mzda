// Aplikace pro výpočet čisté mzdy ze zadané hrubé mzdy

package cz.kerslager.android.mzda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    // výpočet čisté mzdy ze zadané hrubé mzdy (po stisknutí tlačítka)
    public void onButtonKlik(View view) {
        // https://www.vypocet.cz/popis-vypoctu-ciste-mzdy
        long hrubamzda = 0;
        try {
            hrubamzda = Integer.parseInt(editTextHrubaMzda.getText().toString());
        } catch (NumberFormatException e) {
            showToast(getResources().getString(R.string.neplatny_vstup));
        }
        Double zdravotni = hrubamzda * 0.09;
        Double socialni = hrubamzda * 0.25;
        long superhruba = hrubamzda + zdravotni.intValue() + socialni.intValue();
        // zaokrouhlit superhrubou mzdu na stovky nahoru
        // superhruba je celé číslo, takže výpočet probíha celočíselně 
        superhruba = ((superhruba + 100) / 100) * 100;
        // záloha na dan musí být zaokrouhlená na koruny
        long zalohanadanzprijmu = Math.round(superhruba * 0.15);
        zalohanadanzprijmu -= 2070; // sleva na poplatníka
        Double zamsocialni = hrubamzda * 0.065;
        Double zamzdravotni = hrubamzda * 0.045;
        long CistaMzda = hrubamzda - zalohanadanzprijmu
                - zamsocialni.intValue() - zamzdravotni.intValue();
        textViewCistaMzda.setText(Long.toString(CistaMzda));
        // při 11000 hrubého musí vyjít 9640 (pouze při slevě na poplatníka)
    }

    // zobrazení Options Menu v aktuální aktivitě
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater items = getMenuInflater();
        items.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // reakce na výběr položky v Options Menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_oprogramu:
                // zobrazíme zprávu
                showToast(getResources().getString(R.string.nejlepsi_program));
                break;
            case R.id.menu_ukoncit:
                // uzavření aktivity (ukončení programu)
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // zobrazit toast zprávu
    private void showToast(String zprava) {
        Toast myToast = Toast.makeText(
                getApplication(), zprava,
                Toast.LENGTH_LONG
        );
        myToast.show();
    }
}
