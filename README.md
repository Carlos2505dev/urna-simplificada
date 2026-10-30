# Urna Eletrônica Simplificada

Simulador de urna eletrônica em Java (Swing), organizado em MVC (model / view / controller)
com camada de serviço e repositórios. Os votos são salvos localmente e, quando configurado,
também são enviados ao Firebase Firestore (NoSQL).

## Como usar (fluxo da urna)

- **Iniciar**: digite `99999` e aperte **CONFIRMA**. Aparece o relatório inicial, com todos os votos zerados.
- **Votar**: digite os 5 dígitos do candidato; as informações (foto, número, nome, partido) aparecem.
  Aperte **CONFIRMA** para votar. Cada eleitor vota em Vereador e depois em Prefeito.
- **Corrigir**: **CORRIGE** apaga os números digitados.
- **Branco**: **BRANCO** registra voto em branco (confirme em seguida).
- **Número inexistente**: a urna avisa que o voto será nulo se confirmado.
- **Encerrar**: digite `99999` e aperte **CONFIRMA** (sem voto em andamento). Aparece o relatório final.

## Estrutura Atual

    src/main/java/br/edu/urna
      Main.java                         ponto de entrada
      model/                            entidades e regras de dados (Candidate, Office, ElectionReport...)
      util/VotingConstants.java         constantes (interface)
      repository/                       contratos de acesso a dados (interfaces)
      repository/firebase/              implementação com Firestore
      service/VotingService.java        regras da eleição
      controller/VotingController.java  fluxo de votação (teclado)
      view/                             interface gráfica Swing
