package tortevois.projet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import tortevois.projet.R;
import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Message;

public class MessagesAdapter extends BaseAdapter {
    private List<Message> messages;
    private Context context;

    private int appCustomerId = 0;
    private final int listId = R.layout.item_message;
    private final int containerMessage = R.id.msg_container;
    private final int dateId = R.id.msg_date;
    private final int messageId = R.id.msg_content;
    private final SimpleDateFormat formatter = new SimpleDateFormat(ConfigAPI.DATE_FORMAT);


    public MessagesAdapter(List<Message> messages, Context context, int appCustomerId) {
        this.messages = messages;
        this.context = context;
        this.appCustomerId = appCustomerId;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int pos) {
        return messages.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = (Message) getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(listId, parent, false);
        TextView tvMessage = (TextView) convertView.findViewById(messageId);
        tvMessage.setText(message.getContent());

        TextView tvDate = (TextView) convertView.findViewById(dateId);
        tvDate.setText(formatter.format(message.getDateMessage()));

        ConstraintLayout constraintLayout = convertView.findViewById(containerMessage);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (message.getIdCustomer() == appCustomerId) {
            // it's mine, align to right
            constraintSet.clear(messageId, ConstraintSet.LEFT);
            constraintSet.connect(messageId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            constraintSet.clear(dateId, ConstraintSet.LEFT);
            constraintSet.connect(dateId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            tvMessage.setBackgroundResource(R.drawable.msg_right);
            tvMessage.setTextColor(context.getColor(R.color.msg_txt_right));
            tvMessage.setPadding(15, 15, 30, 30);
            tvDate.setGravity(Gravity.RIGHT);
        } else {
            // not mine, align to left (default)
            tvMessage.setBackgroundResource(R.drawable.msg_left);
            tvMessage.setTextColor(context.getColor(R.color.msg_txt_left));
            tvMessage.setPadding(30, 15, 15, 30);
        }
        constraintSet.applyTo(constraintLayout);
        return convertView;
    }
}