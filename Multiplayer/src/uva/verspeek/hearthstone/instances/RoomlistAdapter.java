package uva.verspeek.hearthstone.instances;

import java.util.ArrayList;

import uva.verspeek.hearthstone.activities.RoomlistActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.verspeek.hearthstone.R;
import com.verspeek.hearthstone.R.id;
import com.verspeek.hearthstone.R.layout;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

public class RoomlistAdapter extends BaseAdapter {

	
	private ArrayList<String> roomIdList = new ArrayList<String>();
	private ArrayList<String> roomNameList = new ArrayList<String>();
	private Context context;
	private RoomlistActivity roomlistActivity;
	
	public RoomlistAdapter(Context c){
		this.context = c;
		roomlistActivity = (RoomlistActivity)context;
	}
	
	@Override
	public int getCount() {	
		return roomIdList.size();
	}
	
	public void setData(RoomData[] roomData){
		roomIdList.clear();
		roomNameList.clear();

		for(int i=0;i<roomData.length;i++){
			roomIdList.add(roomData[i].getId());
			roomNameList.add(roomData[i].getName());
		}
		notifyDataSetChanged();
	}
	public void clear(){
		roomIdList.clear();
		roomNameList.clear();

		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int number) {
		return roomIdList.get(number);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.item_room, null);
        }
        if (roomIdList != null) {
        	TextView roomId = (TextView) convertView.findViewById(R.id.item_roomId);
        	Button joinButton = (Button) convertView.findViewById(R.id.item_joinButton);
        	roomId.setText(roomNameList.get(position));
        	joinButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					roomlistActivity.joinRoom(roomIdList.get(position), true);
				}
			});
        }
        return convertView;	
	}

}
