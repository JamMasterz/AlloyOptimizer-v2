package com.jam.alloyoptimizer;

import com.jam.alloyoptimizer.R;

import optimizer.Reference;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ResAmountDialog extends DialogFragment implements OnEditorActionListener{
	DialogClickListener callback;
	EditText amount, stacks, rest;
	AlertDialog dialog;

	public static ResAmountDialog newInstance(int title) {
		ResAmountDialog frag = new ResAmountDialog();
		Bundle args = new Bundle();
		args.putInt("title", title);
		frag.setArguments(args);
		return frag;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstance){
		//Build the dialog, set the ores Image
		AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
		LayoutInflater li = LayoutInflater.from(getActivity());
		View dialogView = li.inflate(R.layout.res_dialog_layout, null);
		ImageView resIcon = (ImageView) dialogView.findViewById(R.id.resourceIcon1);
		resIcon.setImageResource(this.getArguments().getInt("image"));
		myDialog.setView(dialogView);
		myDialog.setTitle(R.string.res_dialog_title);
		
		amount = (EditText) dialogView.findViewById(R.id.amountEdit1);
		stacks = (EditText) dialogView.findViewById(R.id.calc_stacks_edit);
		rest = (EditText) dialogView.findViewById(R.id.calc_rest_edit);
		amount.setImeOptions(EditorInfo.IME_ACTION_DONE);
		stacks.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		stacks.setNextFocusDownId(R.id.calc_rest_edit);
		rest.setImeOptions(EditorInfo.IME_ACTION_DONE);
		
		amount.setOnEditorActionListener(this);
		rest.setOnEditorActionListener(this);
		
		//if the current amount of the ore/ingot isn't 0, put it in the editBox for the user to see
		if (getArguments().getInt("amount") != 0)
			amount.setText(Integer.toString(getArguments().getInt("amount")));
		
		//set up buttons
		myDialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//when user presses OK, check if he typed something in the amount field(priorized over the stack/rest ones)
				if (amount.getText().toString().equals("")){
					//if nothing is in the amount field, check if the stack/rest field contain something
					if (!(stacks.getText().toString().equals("") && rest.getText().toString().equals(""))){
						//if they contain something, check if its 0 and do the calculation
						int stacksInt = (stacks.getText().toString().equals("")) ? 0 : Integer.valueOf(stacks.getText().toString());
						int restInt = (rest.getText().toString().equals("")) ? 0 : Integer.valueOf(rest.getText().toString());
						int calcAmount = Reference.ORE_STACK_SIZE * stacksInt + restInt;
						callback.onOkClick(Integer.toString(calcAmount), getArguments().getInt("index"), getArguments().getBoolean("ingot"));
					}
				} else {
					callback.onOkClick(amount.getText().toString(), getArguments().getInt("index"), getArguments().getBoolean("ingot"));
				}
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
				View v = getActivity().getCurrentFocus();
				if (v != null){
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		myDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			//when user presses Cancel, dismiss the dialog
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
				View v = getActivity().getCurrentFocus();
				if (v != null){
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				dialog.dismiss();
			}
		});
		myDialog.setNeutralButton(R.string.res_dialog_clear, new DialogInterface.OnClickListener() {
			//when user presses Clear, clear the amount of selected ore/ingot
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onClearCLick(Integer.toString(0), getArguments().getInt("index"), getArguments().getBoolean("ingot"));
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
				View v = getActivity().getCurrentFocus();
				if (v != null){
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		
		dialog = myDialog.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		return dialog;
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//if we click done, it will automatically close the dialog for us and the send the values;
		if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE)
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
		return false;
	}
}
