package cashback.mongolitico.data.stores;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import cashback.mongolitico.data.CashbackMongoStorage;
import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.customer.Customer;

import cashbackDomain.factories.CustomerFactory;
import cashbackDomain.objects.identity.Id;

import cashbackDomain.purchase.Order;

public class MongoCustomerStore extends CashbackMongoStorage<Customer> {

	public MongoCustomerStore(MongoDataAcess dataAcess, MongoCollection<Document> collection,
			CustomerFactory objectFactory) {
		super(dataAcess, collection, objectFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Customer readObjectFromCursor(Document currentObject, Id<Customer> id, Integer dbId) {
		//Customer.FIELD_1_NAME_CUSTOMER_NAME;
		// leitura dos itens da compra
		String name = (String)currentObject.get(Customer.FIELD_1_NAME_CUSTOMER_NAME);
		
		//Customer.FIELD_2_NAME_ORDERS_IDS;
		// leitura da lista de compras deste cliente
		Object object = currentObject.get(Customer.FIELD_2_NAME_ORDERS_IDS);
		
		@SuppressWarnings("unchecked")
		ArrayList<Integer> orderIdsInt = (ArrayList<Integer>) object;
		List<Id<Order>> orderIds = orderIdsInt.stream().map(i -> new Id<Order>(i)).collect(Collectors.toList());
		// pegou a lista de compras do cliente!

		Customer customer = getFactory().buildCustomer(name);
		customer.getOrderIds().addAll(orderIds);
		
//		Order order = getFactory().buildOrder(customer, when);
//		order.getItems().addAll(itemIds);
//		order.setCashback(cashback);
//		order.setTotal(total);

		if (customer.getId().getId() == dbId) {
			return customer;
		} else {
			throw new IllegalStateException("Os ids estão inconsistentes");
		}
	}
	
	private CustomerFactory getFactory () {
		return (CustomerFactory) objectFactory;
	}
}
