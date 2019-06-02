import argparse

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument('--number-colors', dest="number_colors",  type=int, default=7)
    parser.add_argument('--number-persons', dest="number_persons",  type=int, default=80)
    args = parser.parse_args()
    colors = []
    for i in range(args.number_colors):
        color = dict()
        color['name'] = 'Color{}'.format(i+1)
        color['id'] = i+1
        colors.append(color)

    persons = []
    for i in range(args.number_persons):
        person = dict()
        person['lastname'] = 'LN{}'.format(i+1)
        person['firstname'] = 'FN{}'.format(i+1)
        person['zipcode'] = '1{0:04d}'.format(i+1)
        person['city'] = 'City{}'.format(i+1)
        person['color_id'] = (i % args.number_colors)+1
        person['id'] = i+1
        persons.append(person)

    insert_colors = "INSERT INTO color(id, name) VALUES\n"
    for c in colors:
        insert_colors += "({}, '{}'),\n".format(c['id'], c['name'])
    insert_colors = insert_colors[:-2] + ";"


    insert_persons = "INSERT INTO person(id, color_id, firstname, lastname, zipcode, city) VALUES\n"
    for p in persons:
        insert_persons += "({}, {}, '{}', '{}', '{}', '{}'),\n".format(p['id'], p['color_id'], p['firstname'], p['lastname'], p['zipcode'], p['city'])

    insert_persons = insert_persons[:-2] + ";"

    print(insert_colors)
    print()
    print(insert_persons)
