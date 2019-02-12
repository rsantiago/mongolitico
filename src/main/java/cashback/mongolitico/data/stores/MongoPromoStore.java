package cashback.mongolitico.data.stores;

import java.time.DayOfWeek;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import cashback.mongolitico.data.CashbackMongoStorage;
import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.album.Genre;
import cashbackDomain.factories.AbstractCashbackObjectsFactory;
import cashbackDomain.factories.PromoFactory;
import cashbackDomain.objects.identity.Id;
import cashbackDomain.promo.Promo;

public class MongoPromoStore extends CashbackMongoStorage<Promo> {

	public MongoPromoStore(MongoDataAcess dataAcess, MongoCollection<Document> collection,
			AbstractCashbackObjectsFactory<Promo> objectFactory) {
		super(dataAcess, collection, objectFactory);
		// TODO Auto-generated constructor stub
	}

	private PromoFactory getPromoFactory() {
		return (PromoFactory) objectFactory;
	}

	@Override
	public Promo save(Promo t) {
		Id<Genre> genreId = t.getGenreId();
		Genre genre = dataAcess.getGenre(genreId);
		if (genre == null) {
			throw new NoSuchElementException("Gênero do id em questão não está cadastrado!");
		} else {
			return super.save(t);
		}
	}

	@Override
	protected Promo readObjectFromCursor(Document currentObject, Id<Promo> id, Integer dbId) {
		// eu sei que são 3 registros:

		Integer genreId = ((Integer) currentObject.get(Promo.FIELD_1_NAME_PROMO_GENRE_ID));
		Integer promoDay = (Integer) currentObject.get(Promo.FIELD_2_NAME_PROMO_DAY);
		Float promoAmount = (Float) ((Double) currentObject.get(Promo.FIELD_3_NAME_PROMO_AMOUNT)).floatValue();

		Genre genre = dataAcess.getGenre(new Id<Genre>(genreId.intValue()));
		PromoFactory f = getPromoFactory();
		DayOfWeek day = DayOfWeek.of(promoDay.intValue());
		Promo newPromo = f.buildPromo(promoAmount, day, genre);

		if (newPromo.getId().getId() == dbId) {
			return newPromo; // identificadores iguais, retorna o objeto lido e instanciado
		} else {
			throw new NoSuchElementException("O identificador do banco é diferente do identificador do construtor");
		}
	}

	public Promo getPromo(DayOfWeek day, Id<Genre> genreId) {
		Iterator<Document> cursor = collection
				.find(Filters.and(Filters.eq(Promo.FIELD_2_NAME_PROMO_DAY, day.getValue()),
						Filters.eq(Promo.FIELD_1_NAME_PROMO_GENRE_ID, genreId.getId())))
				.iterator();

		if (cursor.hasNext()) {
			Document currentPromo = cursor.next();
			int promoId = (Integer) currentPromo.get(Promo.ID_FIELD_NAME);
			// promo id e dbid são o mesmo valor, poruqe estão ambos sendo lidos do banco de
			// dados
			Id<Promo> id = new Id<Promo>(promoId);
			return readObjectFromCursor(currentPromo, id, promoId);
		}
		return null; // caso o cursor esteja vazio
	}

}
