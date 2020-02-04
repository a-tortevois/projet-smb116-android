<?php
// if( DEBUG ) echo "REQUEST_METHOD : POST <br />";
switch( count($query) ) {
	case 1 :
		switch( $query[0] ) {
			// POST ~/customers
			case 'customers' :
				if( is_var($json_in->contract_id) 
					&& is_var($json_in->contract_key)
					&& is_var($json_in->lastname)
					&& is_var($json_in->name)
				) {
					$sql = 'INSERT INTO smb116_customer (contract_id, contract_key, lastname, name) VALUES(:contract_id, :contract_key, :lastname, :name)';
					if( $req = $pdo->prepare($sql) ) {
						$req->bindValue(':contract_id', $json_in->contract_id, PDO::PARAM_STR);
						$req->bindValue(':contract_key', $json_in->contract_key, PDO::PARAM_STR);
						$req->bindValue(':lastname', $json_in->lastname, PDO::PARAM_STR);
						$req->bindValue(':name', $json_in->name, PDO::PARAM_STR);					
					}
				} else {
					$json_out = set_error('CHECK_FIELD');
				}	
			break;

			// POST ~/equipments
			case 'equipments' :
				if( is_var($json_in->id_customer) 
					&& is_var($json_in->serial_number)
					&& is_var($json_in->comment)
					&& isset($json_in->lat)
					&& isset($json_in->lng)
				) {
					$sql = 'INSERT INTO smb116_equipment (id_customer, serial_number, comment, lat, lng) VALUES(:id_customer, :serial_number, :comment, :lat, :lng)';
					if( $req = $pdo->prepare($sql) ) {
						$req->bindValue(':id_customer', $json_in->id_customer, PDO::PARAM_STR);
						$req->bindValue(':serial_number', $json_in->serial_number, PDO::PARAM_STR);
						$req->bindValue(':comment', $json_in->comment, PDO::PARAM_STR);
						$req->bindValue(':lat', $json_in->lat, PDO::PARAM_STR);		
						$req->bindValue(':lng', $json_in->lng, PDO::PARAM_STR);							
					}
				} else {
					$json_out = set_error('CHECK_FIELD');
				}	
			break;

			// POST ~/tickets
			case 'tickets' :
				if( is_var($json_in->id_equipment) 
					&& is_var($json_in->subject)
					&& is_var($json_in->status)
					&& is_var($json_in->date_status)
				) {
					$sql = 'INSERT INTO smb116_ticket (id_equipment, subject, status, date_status) VALUES(:id_equipment, :subject, :status, :date_status)';
					if( $req = $pdo->prepare($sql) ) {
						$req->bindValue(':id_equipment', $json_in->id_equipment, PDO::PARAM_STR);
						$req->bindValue(':subject', $json_in->subject, PDO::PARAM_STR);
						$req->bindValue(':status', $json_in->status, PDO::PARAM_STR);
						$req->bindValue(':date_status', $json_in->date_status, PDO::PARAM_STR);					
					}
				} else {
					$json_out = set_error('CHECK_FIELD');
				}	
			break;
			
			// POST ~/messages
			case 'messages' :
				if( is_var($json_in->id_ticket) 
					&& is_var($json_in->id_customer)
					&& is_var($json_in->date_message)
					&& is_var($json_in->content)
				) {
					$sql = 'INSERT INTO smb116_message (id_ticket, id_customer, date_message, content) VALUES(:id_ticket, :id_customer, :date_message, :content)';
					if( $req = $pdo->prepare($sql) ) {
						$req->bindValue(':id_ticket', $json_in->id_ticket, PDO::PARAM_STR);
						$req->bindValue(':id_customer', $json_in->id_customer, PDO::PARAM_STR);
						$req->bindValue(':date_message', $json_in->date_message, PDO::PARAM_STR);
						$req->bindValue(':content', $json_in->content, PDO::PARAM_STR);					
					}
				} else {
					$json_out = set_error('CHECK_FIELD');
				}	
			break;
		}
	break;
}

if( $json_out['status'] == 0 ) { // no error
	if( !empty($sql) ) {
		if( $req ) {
			if( $req->execute() ) {
				// data inserted successfully
				switch( $query[0] ) {
					case 'customers' :
						$key = 'id_customer';
					break;

					case 'equipments' :
						$key = 'id_equipment';
					break;

					case 'tickets' :
						$key = 'id_ticket';
					break;
					
					case 'messages' :
						$key = 'id_message';
					break;
				}
				$json_out['status'] = "1";
								
				// if( DEBUG ) echo 'lastInsertId: '.$pdo->lastInsertId().'<br/>';
				$json_out['msg'] = array( $key => $pdo->lastInsertId() );
				
				if( $query[0] == 'messages' ) {
					$id_message = $pdo->lastInsertId();
					$id_ticket = $json_in->id_ticket;
					$id_customer = $json_in->id_customer;
					$topic = '/topics/ticket_'.$id_ticket;
					include("fcm_push.php");
				}
			} else {
				// data not inserted
				$error = $pdo->errorInfo();
				$json_out = set_error('SQL_PDO_EXECUTE', $error[2]); 
			}
		} else {
			$error = $pdo->errorInfo();
			$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
		}
	} else {
		$json_out = set_error('UNKNOWN_POST_QUERY');
	}
}
?>