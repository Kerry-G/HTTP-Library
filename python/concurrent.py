from multiprocessing import Pool
import requests

'''
pip install requests
'''

# Make request
def make_request(name):
    print("Starting up a thread and making a request: " + str(name))
    response = requests.post('http://localhost:8080/data/'+ str(name) + '.txt', str(name))
    print("Ending a thread and receiving a response: " + str(name))
    return response

if __name__ == '__main__':
    # open up request pool with N threads and fire off requests
    with Pool(20) as p:
        print(p.map(make_request, [1,2,3,4,5,6,7,8,9,10]))
        