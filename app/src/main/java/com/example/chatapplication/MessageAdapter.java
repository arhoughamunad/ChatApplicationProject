package com.example.chatapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import com.bumptech.glide.Glide;

// Adaptador personalizado para gestionar una lista de mensajes en un RecyclerView
public class MessageAdapter extends RecyclerView.Adapter<HolderMessage> {
    // Lista para almacenar los mensajes
    private List<MsgRec> ListMsg;
    // Contexto de la aplicación
    private Context c;

    // Constructor que recibe el contexto de la aplicación y una lista de mensajes
    public MessageAdapter(Context c, List<MsgRec> ListMsg) {
        this.c = c;
        this.ListMsg = ListMsg;
    }

    // Método para añadir un mensaje a la lista
    public void addMsg(MsgRec m){
        ListMsg.add(m);
        notifyItemInserted(ListMsg.size());
    }

    // Inflar el layout de cada elemento (mensaje) al crear un ViewHolder
    @Override
    public HolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        // Se infla el layout 'mensaje.xml' para representar cada mensaje
        View v = LayoutInflater.from(c).inflate(R.layout.mensaje, parent, false);
        // Se devuelve un nuevo ViewHolder con la vista inflada
        return new HolderMessage(v);
    }

    // Vincular los datos del mensaje con las vistas dentro del ViewHolder
    @Override
    public void onBindViewHolder(HolderMessage holder, int position) {
        MsgRec message = ListMsg.get(position);
        holder.getUsername().setText(message.getUsername());

        // Verificar si el mensaje no es nulo antes de llamar a isEmpty()
        if (message.getMsg() != null && !message.getMsg().isEmpty()) {
            holder.getMsg().setText(message.getMsg());
            holder.getMsg().setVisibility(View.VISIBLE);
        } else {
            holder.getMsg().setVisibility(View.GONE); // Oculta el TextView del mensaje
        }

        // Verificar si la URL de la imagen no es nula antes de llamar a isEmpty()
        if (message.getPictureMsg() != null && !message.getPictureMsg().isEmpty()) {
            holder.getPictureMsg().setVisibility(View.VISIBLE);
            Glide.with(c).load(message.getPictureMsg()).into(holder.getPictureMsg());
        } else {
            holder.getPictureMsg().setVisibility(View.GONE); // Oculta la imagen si no hay
        }

        // Asignar la hora del mensaje al TextView correspondiente
        Long codetime = ListMsg.get(position).getTime();
        Date d = new Date(codetime);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a"); // (a) nos da el formato de am o pm
        holder.getTime().setText(sdf.format(d));
    }

    // Devolver la cantidad total de elementos (mensajes) en la lista
    @Override
    public int getItemCount() {
        return ListMsg.size();
    }
}
