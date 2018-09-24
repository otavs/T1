Integrantes do grupo:  
	- Otávio César Toma da Silva, RA: 726576  
	- Rafael Sales Pavarina, RA: 726583  
	- Caio Vinicius Barbosa Santos, RA: 726503  
  
Como baixar, instalar e usar o compilador:  
	- O compilador pode ser baixado em    
		https://github.com/Otavios/T1/releases/tag/1.0 (T1.zip)  
	- Não é necessário instalar o compilador, mas o usuário deve ter o Java instalado  
	- Para utilizar o compilador:  
		1. Descompacte o arquivo T1.zip  
		2. Entre na pasta T1 pelo terminal  
		3. Compile seu arquivo da linguagem LA com o comando:  
			java -jar T1.jar nomeDoArquivoLA nomeDoArquivoC  
		4. Um arquivo com o código em C será gerado, com o nome utilizado em nomeDoArquivoC  
		
Como compilar o compilador:  
	- Caso queira compilar o projeto novamente, siga estes passos:  
		1. Baixe a pasta do projeto em  
			https://github.com/Otavios/T1  
		2. Entre na pasta do projeto pelo terminal  
		3. Execute o ANTLR para gerar os arquivos necessários:  
			java org.antlr.v4.Tool -visitor LA.g4  
		4. Abra o projeto pelo NetBeans  
		5. Clique em 'Limpar e Construir Projeto'  
		6. Se o projeto foi construído corretamente então o executável .jar será gerado na pasta 'dist' do projeto  
		  
Para executar o corretor, entre na pasta do projeto pelo terminal e execute o comando:  
	- no Windows:  
		java -jar CorretorTrabalho1Windows/CorretorTrabalho1Windows.jar "java -jar dist/T1.jar" gcc temp casosDeTesteT1 "726576, 726503, 726583" tudo  
	- no Linux:  
		java -jar CorretorTrabalho1/CorretorTrabalho1.jar "java -jar dist/T1.jar" gcc temp casosDeTesteT1 "726576, 726503, 726583" tudo  
		
