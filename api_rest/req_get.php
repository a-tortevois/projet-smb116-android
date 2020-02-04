<?php
// if( DEBUG ) echo "REQUEST_METHOD : GET <br />";
switch( count($query) ) {
	case 1 :
		switch( $query[0] ) {
			// GET ~/customers
			case 'customers' :
				$sql = 'SELECT * FROM smb116_customer';
			break;

			// GET ~/equipments
			case 'equipments' :
				$sql = 'SELECT * FROM smb116_equipment';
			break;

			// GET ~/tickets
			case 'tickets' :
				$sql = 'SELECT * FROM smb116_ticket';
			break;
		}
	break;
	
	case 2 :
		if( $id != null ) {
			switch( $query[0] ) {
				// GET ~/customers/$id
				case 'customers' :
					$sql = 'SELECT * FROM smb116_customer WHERE id_customer = :id';
				break;
				
				// GET ~/equipments/$id
				case 'equipments' :
					$sql = 'SELECT * FROM smb116_equipment WHERE id_equipment = :id';
				break;
				
				// GET ~/messages/$id
				case 'messages' :
					$sql = 'SELECT * FROM smb116_message WHERE id_message = :id';
				break;
			}
		}
	break;
	
	case 3 :
		if( $id != null ) {
			// GET ~/customers/$id/equipments
			if( $query[0] == 'customers' && $query[2] == 'equipments' ) {
				$sql = 'SELECT * FROM smb116_equipment WHERE id_customer = :id';
				break;
			}
			
			// GET ~/customers/$id/tickets
			if( $query[0] == 'customers' && $query[2] == 'tickets' ) {
				// TODO : implement
				$sql = 'SELECT smb116_ticket.* FROM smb116_equipment, smb116_ticket WHERE smb116_equipment.id_equipment = smb116_ticket.id_equipment AND smb116_equipment.id_customer ='.$id; 
				break;
			}

			// GET ~/tickets/$id/equipments
			if( $query[0] == 'tickets' && $query[2] == 'equipments' ) {
				// TODO : implement
				// $sql = 'SELECT * FROM smb116_ticket WHERE id_customer = '.$id; 
				break;
			}
			
			// GET ~/tickets/$id/messages
			if( $query[0] == 'tickets' && $query[2] == 'messages' ) {
				$sql = 'SELECT * FROM smb116_message WHERE id_ticket = :id ORDER BY date_message';
			}
		} else {
			// GET ~/auth/$contract_id/$contract_key
			if( $query[0] == 'auth' ) {
				// TODO : implement secutity
				$sql = 'SELECT * FROM smb116_customer WHERE contract_id = "'.$query[1].'" && contract_key = "'.$query[2].'"';
				// password_hash($this->new_pwd1, PASSWORD_BCRYPT, ["cost" => PWD_HASH_COST]).'"
				// password_verify($this->password, $row->password)
				break;
			}			
		}
	break;
}

if( $json_out['status'] == 0 ) { // no error
	if( !empty($sql) ) {
		if( $req = $pdo->prepare($sql) ) {

			if( $id != null ) {
				$req->bindValue(':id', $id, PDO::PARAM_INT);
			}
			// if( $query[0] == 'auth' )
			
			if( $req->execute() ) {
				if( $req->rowCount() > 0 ) {
					$data = $req->fetchAll(PDO::FETCH_ASSOC);
					$json_out['status'] = "1";
					$json_out['msg'] = $data;
				} else {
					$json_out = set_error('SQL_NO_DATA');
				}
			} else {
				$error = $pdo->errorInfo();
				$json_out = set_error('SQL_PDO_EXECUTE', $error[2]);
			}
		} else {
			$error = $pdo->errorInfo();
			$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
		}
	} else {
		$json_out = set_error('UNKNOWN_GET_QUERY');
	}
}
?>