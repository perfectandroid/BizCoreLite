import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EnglishDatePickerDialog(
    context: Context,
    private val listener: DatePickerDialog.OnDateSetListener,
    private val year: Int,
    private val month: Int,
    private val dayOfMonth: Int
) : DatePickerDialog(context, listener, year, month, dayOfMonth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the locale to English
        val locale = Locale("en")
        Locale.setDefault(locale)
        val config = context.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(config)
        }

        // Override the listener to ensure the locale is set
        setButton(DatePickerDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onDateSet(datePicker, datePicker.year, datePicker.month, datePicker.dayOfMonth)
        }
    }
}
