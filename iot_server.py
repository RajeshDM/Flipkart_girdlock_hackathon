from flask import Flask
from flask import jsonify, request, abort, make_response
app = Flask(__name__)
import socket
from flask import send_file

image_filename = [['1_1.jpeg','1_2.jpeg'],
		  ['2_1.jpeg','2_2.jpeg']]

correponding_data = [[(53.2,'Higher than normal'),(41.1,'Lower than normal')],
		     [(32.2,'Normal'),(41.1,'Just above normal')]]

@app.route('/')
def index():
	return "Welcome to IOT Project server - change the url by adding id and and an id number after that to get data in json format!"

@app.route('/id/<int:trans_id>/time/<int:time_id>', methods=['GET'])
def status(trans_id,time_id):
	'''
	t=False
	for i in db1:
		if i['id']==trans_id:
			return jsonify({'location':i['location'],'density':i['density']})
	'''
	
	return jsonify({'speed':correponding_data[trans_id-1][time_id-1][0],'density':correponding_data[trans_id-1][time_id-1][1] })	

@app.route('/id/<int:marker_id>/get_image/<int:image_id>')
def get_image(marker_id, image_id):

	filename = image_filename[marker_id-1][image_id-1]
	return send_file(filename, mimetype='image/jpeg')

if __name__ == '__main__':
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	sock.bind(('localhost', 0))
	app.run(host= '0.0.0.0')

	port = sock.getsockname()[1]
	sock.close()
	app.run(port=port)
	#app.run(debug=True)
