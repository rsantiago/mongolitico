package cashback.mongolitico.data.stores;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import cashback.mongolitico.data.CashbackMongoStorage;
import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.album.Album;
import cashbackDomain.album.Genre;
import cashbackDomain.factories.GenreFactory;
import cashbackDomain.objects.identity.Id;

public class MongoGenreStore extends CashbackMongoStorage<Genre> {

	public MongoGenreStore(MongoDataAcess dataAcess, MongoCollection<Document> collection, GenreFactory factory) {
		super(dataAcess,collection, factory);
	}

	@Override
	protected Genre readObjectFromCursor(Document currentObject, Id<Genre> id, Integer identificador) {
		// eu sei que são 3 registros:
		
		String genreName = (String) currentObject.get(Genre.FIELD_1_NAME_GENRE_NAME);

		Object object = currentObject.get(Genre.FIELD_2_NAME_ALBUMS_IDS);
		if (object instanceof ArrayList<?>) {
			@SuppressWarnings("unchecked")
			ArrayList<Integer> albumIntegerIds = (ArrayList<Integer>) object;

			Genre builtGenre = getGenreFactory().buildGenre(genreName);
			if (identificador.equals(builtGenre.getId().getId())) {
				List<Id<Album>> albumIds = albumIntegerIds.stream().map(i -> new Id<Album>(i))
						.collect(Collectors.toList());
				builtGenre.getAlbumIds().addAll(albumIds);
				return builtGenre;
			} else {
				throw new NoSuchElementException(
						"O identificador do genero do banco é diferente do identificador do construtor");
			}
		} else {
			throw new NoSuchElementException("A lista de identificadores dos albums não tem o tipo correto. O tipo mostrado é: " + object.getClass().getName());
		}
	}

	private GenreFactory getGenreFactory() {
		return (GenreFactory) objectFactory;
	}
}
