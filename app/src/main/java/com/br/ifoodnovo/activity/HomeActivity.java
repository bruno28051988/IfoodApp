package com.br.ifoodnovo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.br.ifoodnovo.R;

import com.br.ifoodnovo.adapters.AdapterEmpresa;
import com.br.ifoodnovo.helpers.ConfiguracaoFirebase;
import com.br.ifoodnovo.listener.RecyclerItemClickListener;
import com.br.ifoodnovo.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

        //Configuração RecyclerView
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this ));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter(adapterEmpresa);

        //Recuperando empresas
        recuperarEmpresas();

        //Configurando SearchView
        searchView.setHint("Pesquisar restaurantes...");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            //Aqui o sistema espera o usuário terminar de digitar para fazer a pesquisa
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            //Já aqui o sistema começa a ésquisar a partir da primeira letra digitada
            //Pode ser um problema caso o sistema possua muitas empresas cadastradas
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        //Configurando o evento de clique
        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Empresa empresaSelecionada = empresas.get(position);
                                Intent i = new Intent(HomeActivity.this, CardapioActivity.class);

                                //Enviando os dados da empresaSelecionada para Activity Cardapio
                                i.putExtra("empresa", empresaSelecionada);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                ));
    }

    private void pesquisarEmpresas(String minhaPesquisa){
        //Criando a ref
        DatabaseReference empresaRef = firebaseRef.child("empresas");

        //Criando a query
        Query query = empresaRef.orderByChild("nome")
                .startAt(minhaPesquisa + "\uf8ff");

        //Configurando a query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Criando as opções do menu
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        // Configurando a pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    // Verificando a opção selecionada pelo usuário
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            finish();
            startActivity(new Intent(HomeActivity.this, AutenticacaoActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(HomeActivity.this, ConfiguracaoUsuarioActivity.class));
    }


    private void inicializaComponentes(){
        searchView = findViewById(R.id.materialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }

    private void recuperarEmpresas(){
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}