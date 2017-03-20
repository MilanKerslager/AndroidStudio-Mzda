// Aplikace pro výpočet čisté mzdy ze zadané hrubé mzdy

package cz.kerslager.android.mzda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    // seznam odkazů
    private static final String URL_PODLE = "https://www.vypocet.cz/popis-vypoctu-ciste-mzdy";
    private static final String URL_ZDROJ = "https://github.com/MilanKerslager/AndroidStudio-Mzda";
    private static final String URL_SPSSE = "https://www.pslib.cz/";
    private static final String URL_CHYBY = "https://github.com/MilanKerslager/AndroidStudio-Mzda/issues";

    // globální proměnné pro prvky formuláře (Aktivity)
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
                //showToast(getResources().getString(R.string.nejlepsi_program));
                alertView(programInfo());
                break;
            case R.id.menu_basedby:
                // odkaz na stránku, podle které je udělán výpočet čisté
                openWebPage(URL_PODLE);
                break;
            case R.id.menu_source:
                // stránka se zdrojovými kody této aplikace
                openWebPage(URL_ZDROJ);
                break;
            case R.id.menu_chyby:
                // webová stránka pro hlášení chyb
                openWebPage(URL_CHYBY);
                break;
            case R.id.menu_spsse:
                // webové stránky školy
                openWebPage(URL_SPSSE);
                break;
            case R.id.menu_ukoncit:
                // uzavření aktivity (ukončení programu)
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // vytvoření textu pro okno "O programu"
    private String programInfo() {
        return getResources().getString(R.string.program) + ": " +
                getResources().getString(R.string.app_name) + "\n" +
                getResources().getString(R.string.verze) + ": " +
                getMyVersionName() + "\n" +
                getResources().getString(R.string.sestaveno) + ": " +
                getMyBuildDate() + "\n\n" +
                getResources().getString(R.string.pro_vyuku);
    }

    // zjištění verze programu (může být uvedeno ve vlastnostech projektu)
    private String getMyVersionName() {
        Context context = getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String myVersionName = getResources().getString(R.string.nedostupne);
        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return myVersionName;
    }

    // datum a čas, kdy byl balíček APK s programem sestaven
    private String getMyBuildDate() {
        Context context = getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String myVersionName = getResources().getString(R.string.nedostupne);
        try {
            myVersionName = DateFormat.getDateTimeInstance().format(
                    packageManager.getPackageInfo(packageName, 0).lastUpdateTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return myVersionName;
    }

    // plovoucí okno s volitelnou zprávou a jediným tlačítkem "OK" (okno s informací)
    private void alertView(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.o_programu))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                            }
                        }).show();
    }

    // zobrazit toast zprávu
    private void showToast(String zprava) {
        Toast myToast = Toast.makeText(
                getApplication(), zprava,
                Toast.LENGTH_LONG
        );
        myToast.show();
    }

    // otevření odkazu pomocí externí aplikace (pomocí vytvoření objektu Intent)
    private void openWebPage(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (NullPointerException e) {
            showToast(getResources().getString(R.string.zadna_data));
        } catch (android.content.ActivityNotFoundException e) {
            showToast(getResources().getString(R.string.neni_browser));
        }
    }

}
