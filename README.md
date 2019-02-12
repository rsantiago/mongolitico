# mongolitico
Camada de persistência com mongodb

## Principais classes e pacotes ##

### cashback.mongolitico.data ###

Pacote utilizado para implementar a base da interação com o MongoDB.

** CashbackMongoStorage **

Implementa KeyValueStore, representando uma tabela ou uma coleção do mesmo documento - albums, promos, customers, cada uma destas entidades são armazenadas em uma KeyValueStore, e no caso da comunicação com o MongoDB estas coleções são gerenciadas por esta classe.

** MongoDataAccess ** 

Mantém uma `CashbackMongoStorage` para cada tipo de entidade do domínio do negócio (uma storage para `Album` outra para `Customer` etc).

### cashback.mongolitico.data.stores ### 

Sobrescrevem a CashbackMongoStorage para os casos específicos em que certas regras devem ser obedecidas na criação de objetos.
Exemplo, quando um `Album` for inserido ou recuperado do MongoDB, a classe `MongoAlbumStore` sabe o que fazer para trabalhar com as partes específicas do objeto album, que diferem de outras entidades - `Customer`, `Item`, `Order` etc.

## cashback.mongolitico.CashbackMongoTest ##

Uma classe para testar mais estensivamente as diferentes facetas do sistema.
Conecta-se remotamente a um cluster MongoDB.

# Observação #

Nesta primeira implementação, o sistema faz uma chamada de cada vez para inserção e leitura de objetos. Com este primeiro passo pronto, o próximo passo é otimizar os processos de comunicação com o banco de dados, para acelerar as queries.
