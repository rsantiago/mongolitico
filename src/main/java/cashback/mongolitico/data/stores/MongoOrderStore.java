package cashback.mongolitico.data.stores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import cashback.mongolitico.data.CashbackMongoStorage;
import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.customer.Customer;
import cashbackDomain.factories.AbstractCashbackObjectsFactory;
import cashbackDomain.factories.OrderFactory;
import cashbackDomain.objects.identity.Id;
import cashbackDomain.purchase.Item;
import cashbackDomain.purchase.Order;

public class MongoOrderStore extends CashbackMongoStorage<Order> {

	public MongoOrderStore(MongoDataAcess dataAcess, MongoCollection<Document> collection,
			AbstractCashbackObjectsFactory<Order> objectFactory) {
		super(dataAcess, collection, objectFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Order readObjectFromCursor(Document currentObject, Id<Order> id, Integer dbId) {
		// eu sei que são 6 registros:
		Date when = (Date) currentObject.get(Order.FIELD_1_NAME_ORDER_WHEN); // data da compra
		float total = ((Double) currentObject.get(Order.FIELD_2_NAME_ORDER_TOTAL)).floatValue(); // total da compra

		// leitura dos itens da compra
		Object object = currentObject.get(Order.FIELD_3_NAME_ORDER_ITEMS_IDS);

		@SuppressWarnings("unchecked")
		ArrayList<Integer> itemsIdsInt = (ArrayList<Integer>) object;
		List<Id<Item>> itemsIds = itemsIdsInt.stream().map(i -> new Id<Item>(i)).collect(Collectors.toList());
		// pegou a lista de itens da compra!

		// pegou o cashback
		float cashback = ((Double) currentObject.get(Order.FIELD_4_NAME_ORDER_CASHBACK)).floatValue();

		// le o cliente
		Id<Customer> customerId = new Id<Customer>((Integer) currentObject.get(Order.FIELD_5_NAME_ORDER_CUSTOMER_ID));
		Customer customer = dataAcess.getCustomer(customerId);
		
		Order order = getFactory().buildOrder(customer , when);
		order.getItems().addAll(itemsIds);
		order.setCashback(cashback);
		order.setTotal(total);
		
		if(order.getId().getId() == dbId) {
			return order;	
		} else {
			throw new IllegalStateException("Os ids estão inconsistentes");
		}	
	}
	
	private OrderFactory getFactory() {
		return (OrderFactory) objectFactory;
	}
}
