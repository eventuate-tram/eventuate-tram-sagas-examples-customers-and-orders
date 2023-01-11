package contracts.http;

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url '/customers/101'
    }
    response {
        status 200
        headers {
            header('Content-Type': 'application/json')
        }
        body('''{"customerId" : "101", "name" : "Chris", "creditLimit" : { "amount" : "123.45" }}''')
    }
}