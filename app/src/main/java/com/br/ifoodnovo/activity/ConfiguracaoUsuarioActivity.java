package com.br.ifoodnovo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.br.ifoodnovo.R;
import com.br.ifoodnovo.helpers.ConfiguracaoFirebase;
import com.br.ifoodnovo.helpers.UsuarioFirebase;
import com.br.ifoodnovo.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class ConfiguracaoUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEndereco;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao_usuario);

        //Inicializando os componentes
        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();


        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //método para validar os dados do usuario
   public void validarDadosUsuario(){
        //valida se os campos foram preenchidos
       String nome = editUsuarioNome.getText().toString();
       String endereco = editUsuarioEndereco.getText().toString();

       if (!nome.isEmpty()){
           if (!endereco.isEmpty()){
               Usuario usuario = new Usuario();
               usuario.setIdUsuario(idUsuario);
               usuario.setNome(nome);
               usuario.setEndereco(endereco);
               usuario.salvar();

               exibirMensagem("Dados atuaizados com sucesso!");
               finish();

           }else{
               exibirMensagem("Digite o seu endereço!");
           }
       }else{
           exibirMensagem("Digite o seu nome!");
       }
   }

    private void exibirMensagem (String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    //método para inicializar os componentes do sistema
    private void inicializarComponentes(){
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
    }
}