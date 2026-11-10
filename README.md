# Urna Eletrônica Simplificada

Simulador de urna eletrônica em Java (Swing), organizado em MVC (model / view / controller)
com camada de serviço e repositórios. Os votos são salvos localmente e, quando configurado,
também são enviados ao Firebase Firestore (NoSQL).

## Como usar (fluxo da urna)

- Rode ".\run-local.ps1" no terminal.
- **Iniciar**: digite `99999` e aperte **CONFIRMA**. Aparece o relatório inicial, com todos os votos zerados.
- **Votar**: digite 5 dígitos para vereador ou 2 dígitos para prefeito; as informações (foto, número, nome, partido) aparecem.
  Aperte **CONFIRMA** para votar. Cada eleitor vota em Vereador e depois em Prefeito.
- **Corrigir**: **CORRIGE** apaga os números digitados.
- **Branco**: **BRANCO** registra voto em branco (confirme em seguida).
- **Número inexistente**: a urna avisa que o voto será nulo se confirmado.
- **Encerrar**: digite `99999` e aperte **CONFIRMA** (sem voto em andamento). Aparece o relatório final.

## Estrutura do projeto

    src/main/java/br/edu/urna
      Principal.java                    ponto de entrada com Firebase
      PrincipalLocal.java               ponto de entrada sem Firebase
      model/                            entidades do domínio
      util/ConstantesVotacao.java       constantes da aplicação
      repository/                       contratos e exceções de persistência
      repository/local/                 persistência local em arquivo TSV
      repository/firebase/              persistência remota com Firestore
      service/ServicoVotacao.java       regras da eleição
      controller/ControladorVotacao.java fluxo de votação
      view/                             interface gráfica Swing

## Validação

O comando `.\run-local.ps1` compila e executa a versão local sem depender das credenciais
do Firebase. Para a versão completa, use `mvn clean package` em um ambiente com Maven
instalado; a dependência do Firebase está declarada no `pom.xml`.
