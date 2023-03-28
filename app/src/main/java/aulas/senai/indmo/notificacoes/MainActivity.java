package aulas.senai.indmo.notificacoes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //Objeto global para representar o gerenciador de notificações do Android
    NotificationManagerCompat gerenciador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        criaCanalNotificacao();
        //Iniciar o "gerenciador"
        gerenciador = NotificationManagerCompat.from(this);

        //Objetos dos botões da tela
        Button btBasico = findViewById(R.id.btBasico);
        Button btImagem = findViewById(R.id.btImagem);
        Button btAction = findViewById(R.id.btAction);

        //Eventos dos botões
        btBasico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Criar um objeto para receber o método que cria a notificação base
                NotificationCompat.Builder base = notificacaoBase("Título", "Mensagem");
                //A notificação base não precisamos acrescentar nada, só pedir para ela exibir
                //O id é responsável por identificar a notificação unicamente dentro do canal
                gerenciador.notify(1, base.build());
            }
        });

        btImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCompat.Builder base = notificacaoBase("Título", "Mensagem");
                //Além da notificação base, vamos adicionar uma imagem
                //Primeiro criamos a imagem
                Bitmap imagem = BitmapFactory.decodeResource(getResources(), R.drawable.android);
                //Depois adicionamos as configurações de estilo para exibir a imagem
                base.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(imagem) //Imagem que será exibida em tamanho maior
                    .bigLargeIcon(null) //Só exibe a imagem quando expandir a notificação
                    );

                //Pedir para a notificação ser exibida
                gerenciador.notify(2, base.build());
            }
        });

        btAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Para usar botões de ação (Action) será necessário direcionar o código
                //para uma classe chamada BroadcastReceiver
                Intent exibeMensagem = new Intent(MainActivity.this, BroadcastMensagem.class);
                PendingIntent pending = PendingIntent.getBroadcast(
                        MainActivity.this, //Contexto
                        0, //Código de requisição (zero por padrão)
                        exibeMensagem, //Intent que será acionada
                        PendingIntent.FLAG_CANCEL_CURRENT);

                //Recupera a notificação base
                NotificationCompat.Builder base = notificacaoBase("Título", "Mensagem");
                //Adicionar um botão de ação
                base.addAction(R.drawable.icone_notificacao, "Exibir mensagem", pending);

                //Exibir a notificação
                gerenciador.notify(3, base.build());
            }
        });

    }

    //Método para configurar uma notificação com os atributos básicos
    //Como toda notificação terá título, ícone, mensagem e uma Intent
    //que irá direcionar a uma tela ou recurso, vamos usar esse método
    //para não ter que reescrever toda vez a mesma configuração padrão
    private NotificationCompat.Builder notificacaoBase(String titulo, String mensagem){
        //Criar uma Intent para direcionar a qual tela/recurso que será aberto ao tocar na notificação
        Intent abrirTela = new Intent(MainActivity.this, TelaDois.class);
        //Agora será necessário transformar essa Intent em pendente, ou seja,
        //uma Intent que ficará aguardando o toque na notificação para ser executada
        PendingIntent pending = PendingIntent.getActivity(
                this, //Contexto
                0, //Código da requisição (zero por padrão)
                abrirTela, //Intent que será transformada em pendente
                PendingIntent.FLAG_CANCEL_CURRENT //Modo de funcionamento (atualiza a notificação)
        );

        //Configurar a notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(
            this, //Contexto
            "1000" //ID do canal de notificação (o mesmo usado no método que cria o canal)
            ).setContentTitle(titulo)
            .setContentText(mensagem)
            .setAutoCancel(true) //Remove a notificação após tocar/clicar
            .setContentIntent(pending) //Indicando o que será aberto após tocar/clicar
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.icone_notificacao);

        return notificacao;
    }



    //Criar um método para verificar qual a versão do Android no dispositivo
    //Caso for o Android 8 ou acima, será necessário criar o canal de notificação
    //O canal de notificação tem a função de agrupar as notificações bem como
    //possibilitar ao usuário que personalize as notificações desse canal como,
    //por exemplo, silenciar as notificações, ocultar, etc
    private void criaCanalNotificacao(){
        //Verificar qual a versão do dispositivo
        //Se a versão for a 8 (Oreo) ou superior
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Cria um nome para o canal (será exibido nas configurações)
            CharSequence nomeCanal = "Canal do aplicativo teste";
            //Cria uma descrição (para explicar a sua finalidade)
            String descricaoCanal = "Canal para notificações do aplicativo teste";

            //Cria o canal
            NotificationChannel canal = new NotificationChannel(
                "1000", //É necessário colocar um ID o qual irá identificar o canal
                nomeCanal,
                NotificationManager.IMPORTANCE_DEFAULT //Nível de prioridade
            );
            //Passar a descrição pois no construtor não tem o parâmetro
            canal.setDescription(descricaoCanal);

            //Por fim, enviar a nossa configuração de canal para o gerenciador
            //de notificações e canais do Android
            NotificationManager gerenciador = getSystemService(NotificationManager.class);
            gerenciador.createNotificationChannel(canal);
        }
    }
}




