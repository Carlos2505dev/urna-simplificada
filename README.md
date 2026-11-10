# Urna Eletrônica Simplificada

Simulador de urna eletrônica em Java (Swing), organizado em MVC (model / view / controller)
com camada de serviço e repositórios. Os votos são salvos localmente e, quando configurado,
também são enviados ao Firebase Firestore (NoSQL).

Autores: Caio Ricardo, Carlos Bezerra, David Souza, Davi Hiel e Marcos Antônio.

## Como usar (fluxo da urna)

- Rode localmente ".\run-local.ps1" ou execute o comando da versão completa abaixo no terminal.
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
do Firebase.

### Instalar o Maven

No PowerShell, execute:

```powershell
$version = '3.9.16'; $base = Join-Path $env:USERPROFILE 'tools'; $archive = Join-Path $env:TEMP "apache-maven-$version-bin.zip"; $install = Join-Path $base "apache-maven-$version"; $url = "https://dlcdn.apache.org/maven/maven-3/$version/binaries/apache-maven-$version-bin.zip"; New-Item -ItemType Directory -Force -Path $base | Out-Null; Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile $archive; Expand-Archive -Path $archive -DestinationPath $base -Force; $mavenBin = Join-Path $install 'bin'; [Environment]::SetEnvironmentVariable('MAVEN_HOME', $install, 'User'); $userPath = [Environment]::GetEnvironmentVariable('Path', 'User'); if (($userPath -split ';') -notcontains $mavenBin) { [Environment]::SetEnvironmentVariable('Path', (($userPath.TrimEnd(';') + ';' + $mavenBin).Trim(';')), 'User') }; & (Join-Path $mavenBin 'mvn.cmd') -version
```

### Executar a versão completa

Na raiz do projeto, execute o comando abaixo. Ele atualiza o `PATH` da sessão atual e
compila e inicia a versão completa com Maven e Swing:

```powershell
$env:FIREBASE_KEY_PATH = (Join-Path (Get-Location) 'firebase-key.json')

$mavenBin = Join-Path $env:USERPROFILE 'tools\apache-maven-3.9.16\bin'
$env:Path = "$mavenBin;$env:Path"

mvn clean package
mvn exec:java "-Dexec.mainClass=br.edu.urna.Principal" "-Dexec.classpathScope=runtime"
```

