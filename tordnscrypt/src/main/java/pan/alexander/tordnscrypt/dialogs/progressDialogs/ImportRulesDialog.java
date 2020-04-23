package pan.alexander.tordnscrypt.dialogs.progressDialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import pan.alexander.tordnscrypt.R;
import pan.alexander.tordnscrypt.dialogs.ExtendedDialogFragment;
import pan.alexander.tordnscrypt.settings.dnscrypt_settings.ImportRules;

public class ImportRulesDialog extends ExtendedDialogFragment implements ImportRules.OnDNSCryptRuleAddLineListener {
    private Thread importThread;
    private int linesAdded = 0;
    private String dialogMessage;
    private int buttonText = R.string.cancel;
    private boolean dialogImportRulesIndeterminate;
    private TextView tvDialogImportRules;
    private ProgressBar pbDialogImportRules;
    private Button btnDialogImportRules;
    private Handler handler = new Handler();

    public static ImportRulesDialog newInstance() {
        return new ImportRulesDialog();
    }

    @Override
    public AlertDialog.Builder assignBuilder() {

        if (getActivity() == null) {
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogTheme);
        builder.setTitle(R.string.import_dnscrypt_rules_dialog_title);

        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_import_dnscrypt_rules, null, false);

        if (view != null) {
            builder.setView(view);
            tvDialogImportRules = view.findViewById(R.id.tvDialogImportRules);
            pbDialogImportRules = view.findViewById(R.id.pbDialogImportRules);
            btnDialogImportRules = view.findViewById(R.id.btnDialogImportRules);
        }

        if (btnDialogImportRules != null) {
            btnDialogImportRules.setOnClickListener(v -> {
                if (importThread != null && importThread.isAlive()) {
                    importThread.interrupt();
                }
                this.dismiss();
            });
        }

        builder.setCancelable(false);
        return builder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tvDialogImportRules != null && pbDialogImportRules != null && btnDialogImportRules != null) {
            if (dialogMessage != null) {
                tvDialogImportRules.setText(dialogMessage);
            }
            pbDialogImportRules.setIndeterminate(dialogImportRulesIndeterminate);
            btnDialogImportRules.setText(buttonText);
        }
    }

    @Override
    public void onDNSCryptRuleLinesAddingStarted(@NonNull Thread importThread) {
        this.importThread = importThread;

        try {
            int count = 0;
            while (count < 15 && (tvDialogImportRules == null || pbDialogImportRules == null)) {
                count++;
                Thread.sleep(100);
            }
        } catch (InterruptedException ignored){}

        if (handler != null) {
            handler.post(() -> {
                if (tvDialogImportRules != null && pbDialogImportRules != null) {
                    dialogMessage = String.format(getString(R.string.import_dnscrypt_rules_dialog_message), linesAdded);
                    tvDialogImportRules.setText(dialogMessage);
                    pbDialogImportRules.setIndeterminate(true);
                    dialogImportRulesIndeterminate = true;
                }
            });
        }
    }

    @Override
    public void onDNSCryptRuleLineAdded(int count) {
        linesAdded = count;
        dialogMessage = String.format(getString(R.string.import_dnscrypt_rules_dialog_message), linesAdded);
        if (tvDialogImportRules != null && handler != null) {
            handler.post(() -> tvDialogImportRules.setText(dialogMessage));
        }
    }

    @Override
    public void onDNSCryptRuleLinesAddingFinished() {

        if (tvDialogImportRules!= null && pbDialogImportRules != null && btnDialogImportRules != null && handler != null) {
            handler.post(() -> {
                dialogMessage = String.format(getString(R.string.import_dnscrypt_rules_complete_dialog_message), linesAdded);
                tvDialogImportRules.setText(dialogMessage);
                pbDialogImportRules.setIndeterminate(false);
                dialogImportRulesIndeterminate = false;
                buttonText = R.string.ok;
                btnDialogImportRules.setText(buttonText);
            });
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (importThread != null && importThread.isAlive()) {
            importThread.interrupt();
        }

        handler = null;
    }
}