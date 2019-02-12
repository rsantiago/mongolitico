package cashback.mongolitico.data.stores;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import cashback.mongolitico.data.CashbackMongoStorage;
import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.album.Album;
import cashbackDomain.factories.AbstractCashbackObjectsFactory;
import cashbackDomain.factories.ItemFactory;
import cashbackDomain.objects.identity.Id;
import cashbackDomain.promo.Promo;
import cashbackDomain.purchase.Item;
import cashbackDomain.purchase.Order;

public class MongoItemStore extends CashbackMongoStorage<Item> {

	public MongoItemStore(MongoDataAcess dataAcess, MongoCollection<Document> collection, AbstractCashbackObjectsFactory<Item> objectFactory) {
		super(dataAcess,collection, objectFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Item readObjectFromCursor(Document currentObject, Id<Item> id, Integer dbId) {
//		Item.FIELD_NAME_1_ITEM_ORDER_ID;
		Id<Order> orderId = new Id<Order>((Integer) currentObject.get(Item.FIELD_NAME_1_ITEM_ORDER_ID));
		
//		Item.FIELD_NAME_2_ITEM_QUANTITY;
		Integer quantity = (Integer) currentObject.get(Item.FIELD_NAME_2_ITEM_QUANTITY);
		
//		Item.FIELD_NAME_3_ITEM_UNIT_PRICE;
		Float price = (Float) ((Double) currentObject.get(Item.FIELD_NAME_3_ITEM_UNIT_PRICE)).floatValue();

//		Item.FIELD_NAME_4_ITEM_CASHBACK
		Float cashback = (Float) ((Double) currentObject.get(Item.FIELD_NAME_4_ITEM_CASHBACK)).floatValue();

//		Item.FIELD_NAME_4_ITEM_ALBUM_ID;
		Id<Album> albumId = new Id<Album>((Integer) currentObject.get(Item.FIELD_NAME_5_ITEM_ALBUM_ID));
		
		Order order = dataAcess.getOrder(orderId);
		Album album = dataAcess.getAlbum(albumId);
		
		if(order==null) {
			throw new NoSuchElementException("Não há pedido para este item");
		} else if(album == null) {
			throw new NoSuchElementException("Não há album para este item");
		} else {
			Item item = getFactory().buildItem(album, order, quantity);
			item.setUnitPrice(price);
			item.setCashback(cashback);
			
			if (album.getId().getId() == dbId) {
				return item;
			} else {
				throw new IllegalStateException("Os ids gerados pela aplicação e os lidos no banco estão inconsistentes");
			}
			
		}
	}

	private ItemFactory getFactory() {
		return (ItemFactory) objectFactory;
	}

	@Override
	public Item save(Item item) {
		Album album = dataAcess.getAlbum(item.getAlbumId());
		Order order = dataAcess.getOrder(item.getOrderId());
		
		if(album == null) {
			throw new NoSuchElementException("tentou adicionar uma compra"+
					" para um album que não existe");
		}
		
		if( order == null ) {
			throw new NoSuchElementException("Tentou adicionar um item"
					+ " para um pedido que não existe");
		}
		
		return super.save(item);
	}
	
}
