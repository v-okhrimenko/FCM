package com.example.fcm.recycleviewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.R;
import com.example.fcm.models.TempaleJob;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TemplateAdapter extends FirestoreRecyclerAdapter<TempaleJob, TemplateAdapter.NoteHolder> {

    String template_name, valuta, tempalte_type;
    Integer price_hour, price_fixed, price_smena, smena_duration, overtime_pocent ;



    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;



    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    //private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    private CollectionReference noteRef_full = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs_full");

    private OnItemClickListener listener;



    public TemplateAdapter(@NonNull FirestoreRecyclerOptions<TempaleJob> options) {
        super( options );

    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull TempaleJob model) {
        //System.out.println( model.getTempalte_type() );
        if(model.getTempalte_type().equals("for smena" )) {
            holder.price_job_id_new.setText( String.valueOf(model.getPrice_smena()) );
            holder.tv_hour.setVisibility( View.INVISIBLE );
            holder.tv_tarif_type.setText( holder.itemView.getContext().getString(R.string.posmennyu_rate) );
            holder.tv_procent.setText( String.valueOf(model.getOvertime_pocent()) );


        }

        if(model.getTempalte_type().equals("for hour" )) {
            holder.price_job_id_new.setText( String.valueOf(model.getPrice_hour()) );
            holder.tv_procent.setVisibility( View.INVISIBLE );
            holder.tv_tarif_type.setText( holder.itemView.getContext().getString(R.string.for_hour) );
            holder.tv_hour.setVisibility( View.VISIBLE );
            holder.symbol_procent.setVisibility(View.INVISIBLE);
            holder.symbol_plus.setVisibility(View.INVISIBLE);
        }

        if(model.getTempalte_type().equals("fixed" )) {
            holder.price_job_id_new.setText( String.valueOf(model.getPrice_fixed()) );
            holder.tv_hour.setVisibility( View.INVISIBLE );
            holder.tv_tarif_type.setText( holder.itemView.getContext().getString(R.string.fixed_rate) );
            holder.tv_procent.setVisibility( View.INVISIBLE );
            holder.symbol_procent.setVisibility(View.INVISIBLE);
            holder.symbol_plus.setVisibility(View.INVISIBLE);
        }

        holder.name_job_id_new.setText( model.getTemplate_name() );
        holder.tv_valuta.setText( model.getValuta() );


        //holder.textViewPrice.setText( String.valueOf(model.getPrice()) );
//        holder.textViewPriority.setText( String.valueOf(  model.getPriority()) );
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.rv_template, parent, false );

        return new NoteHolder( v );

    }

    public void undo(int position){
        TempaleJob TempaleJob = new TempaleJob();
        System.out.println( "CANCEL"+position );
        TempaleJob.setTemplate_name(template_name.toUpperCase());
        TempaleJob.setValuta(valuta);
        TempaleJob.setTempalte_type(tempalte_type);
        TempaleJob.setPrice_smena(price_smena);
        TempaleJob.setPrice_fixed(price_fixed);
        TempaleJob.setPrice_hour(price_hour);
        TempaleJob.setSmena_duration(smena_duration);
        TempaleJob.setOvertime_pocent(overtime_pocent);





        noteRef_full.document(template_name).set( TempaleJob )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                } )

                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                } );


    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot( position ).getReference().delete();

    }

//    public void add_to_undelete_data(int position) {
//
//        System.out.println( "ADD TO UNDELETE"+ position );
//
//
//        TempaleJob TempaleJob = getSnapshots().getSnapshot( position ).toObject( TempaleJob.class );
//        template_name = TempaleJob.getTemplate_name();
//        valuta = TempaleJob.getValuta();
//        tempalte_type = TempaleJob.getTempalte_type();
//        price_hour = TempaleJob.getPrice_hour();
//        price_fixed = TempaleJob.getPrice_fixed();
//        price_smena = TempaleJob.getPrice_smena();
//        smena_duration = TempaleJob.getSmena_duration();
//        overtime_pocent = TempaleJob.getOvertime_pocent();
//
//
//        System.out.println( "DELEEE "+template_name+" "+valuta+ " "+tempalte_type  );
//    }


    public class NoteHolder extends RecyclerView.ViewHolder {

        //TextView textViewName;
        //TextView textViewPrice;

        TextView name_job_id_new;
        TextView tv_tarif_type;
        TextView tv_hour;
        TextView tv_valuta;
        TextView price_job_id_new;
        TextView tv_procent;
        TextView symbol_plus;
        TextView symbol_procent;


        //TextView tv_procent;




        public NoteHolder(@NonNull View itemView) {

            super( itemView );
            name_job_id_new = itemView.findViewById( R.id.name_job_id_new );
            tv_tarif_type = itemView.findViewById( R.id.tv_tarif_type );
            tv_hour = itemView.findViewById( R.id.tv_hour );
            tv_valuta = itemView.findViewById( R.id.tv_valuta );
            price_job_id_new = itemView.findViewById( R.id.price_job_id_new );
            tv_procent = itemView.findViewById( R.id.tv_procent );
            symbol_plus = itemView.findViewById( R.id.symbol_plus );
            symbol_procent = itemView.findViewById( R.id.symbol_procent );
//            textViewPriority = itemView.findViewById( R.id.priority_id );


            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick( getSnapshots().getSnapshot( position ), position );
                    }


                }
            } );
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void  setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
