package cashback.mongolitico.data;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import cashbackDomain.album.Genre;
import cashbackDomain.data.KeyValueStore;
import cashbackDomain.factories.AbstractCashbackObjectsFactory;
import cashbackDomain.objects.identity.Id;
import cashbackDomain.objects.identity.Identifiable;

public abstract class CashbackMongoStorage<T extends Identifiable<T>> extends KeyValueStore<T> {
	/**
	 * Algumas storages precisam de acesso global aos dados. Por isso, o data access vai junto.
	 */
	protected MongoDataAcess dataAcess;
	/**
	 * A coleção de dados com a qual esse objeto vai trabalhar
	 */
	protected MongoCollection<Document> collection;
	protected AbstractCashbackObjectsFactory<T> objectFactory;
	
	public CashbackMongoStorage(MongoDataAcess dataAcess, MongoCollection<Document> collection, AbstractCashbackObjectsFactory<T> objectFactory) {
		this.collection = collection;
		this.objectFactory = objectFactory;
		this.dataAcess = dataAcess;
	}
	
	
	@Override
	public Set<Id<T>> getAllIds() {
		//recupera todos os objetos do banco
		
		MongoCursor<Document> cursor = collection.find() // procura todos os documentos
				.projection( // com as seguintes projeções:
					Projections.fields(Projections.include(Identifiable.ID_FIELD_NAME), // inclui o campo id 
					Projections.excludeId())) // exclui o identificador do mongo
						.iterator(); // recupera o cursor
		
		Set<Id<T>> result = new HashSet<Id<T>>();
		
		// preparando objetos para iteração
		Integer id = null;
		Document nextDocument = null;
		
		while(cursor.hasNext()) {
			nextDocument = cursor.next();
			id = (Integer)nextDocument.get(Identifiable.ID_FIELD_NAME);
			result.add(new Id<T>(id));
		}
		
		return result;
	}
	
	protected Set<Id<T>> extractIdsFromCursor(MongoCursor<Document> doc) {
		//while(doc.hasNext()) {
			return new HashSet<Id<T>>();

		//}
	}

	@Override
	public T getById(Id<T> id) {
		FindIterable<Document> iterable = collection.find( // procura todos os documentos com as seguintes caracteristicas:
				Filters.eq(Identifiable.ID_FIELD_NAME, id.getId())). // identificador igual ao passado como parametro
				projection(Projections.excludeId()); // exclua o id do mongodb
		
		MongoCursor<Document> cursor = iterable.iterator(); // recupera o iterador
		
		if(cursor.hasNext()) {
			Document currentObject = cursor.next();
			Integer identificador = (Integer) currentObject.get(Genre.ID_FIELD_NAME); // lê o identificador e passa como parâmetro do construtor do objeto
			return readObjectFromCursor(currentObject, id, identificador); // leia o objeto a partir deste cursor
			
		} else {
			return null; // não há objeto para o id selecionado
		}
	}

	@Override
	public Set<T> getAll() {
		Set<Id<T>> ids = extractIdsFromCursor(collection.find().projection(Projections.fields(Projections.include(Identifiable.ID_FIELD_NAME),Projections.excludeId())).iterator());
		return getByIds(ids); 
	}
	
	
	protected void saveInDataStructure(T toSave) {
		Document objectDocument = new Document(toSave.toHashMap());
		collection.insertOne(objectDocument);
	}

	protected void deleteAllObjects() {
		collection.deleteMany(new Document());
	}
	
	/**
	 * Retorna um cursor contendo todos os objetos encontrados
	 * para a lista de ids passada como parâmetro
	 * 
	 * @param ids Identificadores do objeto que você deseja encontrar
	 * @return O documento com as informações do objeto procurado
	 */
	public MongoCursor<Document> mongoFind(Set<Id<T>> ids) {
		Set<Integer> intSet = ids.parallelStream().map(i -> i.getId()).collect(Collectors.toSet());
		return collection.find( // encontre no documento
				Filters.in(Identifiable.ID_FIELD_NAME,intSet)) // tudo que estiver dentro desta lista de ids
				.iterator(); // recupera o iterador
	}
	
	@Override
	public Set<T> getByIds(Set<Id<T>> ids) {
		MongoCursor<Document> cursor = mongoFind(ids);
		Set<T> result = new HashSet<T>();
		
		Document currentObject = null;
		
		for (Id<T> id : ids) {
			currentObject = cursor.next();
			Integer dbId = (Integer) currentObject.get(Genre.ID_FIELD_NAME);
			if(id.getId()!= dbId) {
				
			}
			result.add(readObjectFromCursor(currentObject, id, dbId));
		}
		
		
		
		return result;
	}

	/**
	 * construtor do objeto a partir de um cursor
	 *
	 * @param id O identificador do objeto
	 * @param currentObject O cursor com o objeto atual que está sendo lido do banco de dados
	 * @param dbId O identificador que o banco recuperou para este objeto (é necessário fazer um 
	 * teste se a factory internamente consegue gerar um identificador identico ao do objeto que 
	 * já está no banco. Lança uma exceção, caso os ids sejam diferentes.
	 * Isto garante que a factory esteja sempre criando ids únicos na lógica da aplicação,
	 * ao invés de delegar para o banco de dados.
	 * @return O objeto instanciado com as informações do cursor de entrada
	 */
	protected abstract T readObjectFromCursor(Document currentObject, Id<T> id, Integer dbId) ;
	
}
