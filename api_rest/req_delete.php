<?php
// if( DEBUG ) echo "REQUEST_METHOD : DELETE <br />";
switch( count($query) ) {
	case 2 :
		switch( $query[0] ) {
			// DELETE ~/customers/$id
			case 'customers' :
				$sql = "DELETE FROM smb116_customer WHERE id_customer = :id";
				// TODO : DELETE dependancies
			break;

			// DELETE ~/equipments/$id
			case 'equipments' :
				$sql = "DELETE FROM smb116_equipment WHERE id_equipment = :id";
				// TODO : DELETE dependancies
			break;

			// DELETE ~/tickets/$id
			case 'tickets' :
				$sql = "DELETE FROM smb116_ticket WHERE id_ticket = :id";
				// TODO : DELETE dependancies
			break;
				
			// PUT ~/messages/$id
			case 'messages' :
				$sql = "DELETE FROM smb116_message WHERE id_message = :id";
				// TODO : DELETE dependancies
			break;
		}
	break;
}

if( $json_out['status'] == 0 ) { // no error
	if( !empty($sql) ) {
		if( $req = $pdo->prepare($sql) ) {
			$req->bindValue(':id', $id, PDO::PARAM_INT);	
			if( $req->execute() ) {
				// data deleted successfully
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
				$json_out['msg'] = array( $key => $id );
			} else {
				// data not deleted
				$error = $pdo->errorInfo();
				$json_out = set_error('SQL_PDO_EXECUTE', $error[2]); 
			}
		} else {
			$error = $pdo->errorInfo();
			$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
		}
	} else {
		$json_out = set_error('UNKNOWN_PUT_QUERY');
	}
}
?>