package cashback.mongolitico.data.stores;

import java.util.NoSuchElementException;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import cashback.mongolitico.data.CashbackMongoStorage;
import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.album.Album;
import cashbackDomain.album.Genre;
import cashbackDomain.factories.AlbumFactory;
import cashbackDomain.objects.identity.Id;

public class MongoAlbumStore extends CashbackMongoStorage<Album> {

	public MongoAlbumStore(MongoDataAcess dataAcess, MongoCollection<Document> collection, AlbumFactory albumFactory) {
		super(dataAcess,collection, albumFactory);
	}

	@Override
	protected Album readObjectFromCursor(Document currentObject, Id<Album> id, Integer dbId) {
//		Album.FIELD_1_NAME_ALBUM_NAME;
		String albumName = (String)currentObject.get(Album.FIELD_1_NAME_ALBUM_NAME);
		
//		Album.FIELD_2_NAME_ALBUM_ARTIST;
		String artistName = (String)currentObject.get(Album.FIELD_2_NAME_ALBUM_ARTIST);

//		Album.FIELD_3_NAME_PRICE;
		Float price = (Float) ((Double) currentObject.get(Album.FIELD_3_NAME_PRICE)).floatValue();
		
//		Album.FIELD_4_NAME_GENRE_ID;
		Id<Genre> genreId = new Id<Genre>((Integer) currentObject.get(Album.FIELD_4_NAME_GENRE_ID));

		Genre genre = dataAcess.getGenre(genreId);
		if(genre == null) {
			throw new NoSuchElementException("Genero ainda nõa existe no sistema!");
		} else { 
			Album album = getFactory().buildAlbum(artistName, albumName, genreId);
			album.setPrice(price);

			if (album.getId().getId() == dbId) {
				return album;
			} else {
				throw new IllegalStateException("Os ids gerados pela aplicação e os lidos no banco estão inconsistentes");
			}
		}
	}


	private AlbumFactory getFactory() {
		return (AlbumFactory) objectFactory;
	}
	
	@Override
	public Album save(Album t) {
		Genre genre = this.dataAcess.getGenre(t.getGenreId());
		if(genre==null) {
			throw new NoSuchElementException("Tentou adicionar album com um genero que não existe");
		} else {
			return super.save(t);
		}
	}
}
