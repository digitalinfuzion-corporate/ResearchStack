package co.touchlab.researchstack.backbone.ui.step.body;

import android.content.res.Resources;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.backbone.R;
import co.touchlab.researchstack.backbone.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.utils.ViewUtils;

public class TextQuestionBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep       step;
    private StepResult<String> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText     editText;

    public TextQuestionBody(Step step, StepResult result)
    {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        View body = inflater.inflate(R.layout.compact_item_edit_text, parent, false);

        editText = (EditText) body.findViewById(R.id.value);
        editText.setHint(R.string.rsb_hint_step_body_text);

        TextView title = (TextView) body.findViewById(R.id.label);

        if(viewType == VIEW_TYPE_COMPACT)
        {
            title.setText(step.getTitle());
        }
        else
        {
            title.setVisibility(View.GONE);
        }

        // Restore previous result
        String stringResult = result.getResult();
        if(! TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }

        // Set result on text change
        RxTextView.textChanges(editText).subscribe(text -> {
            result.setResult(text.toString());
        });

        // Format EditText from TextAnswerFormat
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        editText.setSingleLine(! format.isMultipleLines());

        if(format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH)
        {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        body.setLayoutParams(layoutParams);

        return body;
    }


    @Override
    public StepResult getStepResult()
    {
        return result;
    }

    @Override
    public boolean isAnswerValid()
    {
        return ((TextAnswerFormat) step.getAnswerFormat()).isAnswerValid(editText.getText()
                .toString());
    }

}
