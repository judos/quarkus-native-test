import mysql from 'mysql2/promise';
import bcrypt from 'bcryptjs';
import { customers, invoices, revenue, users } from '../lib/placeholder-data';
//
// import mysql from 'mysql';
// const connection = mysql.createConnection({
// 	host: process.env.MYSQL_URL!,
// 	user: process.env.MYSQL_USER!,
// 	password: process.env.MYSQL_PW!
// });
// connection.connect(function(err: { stack: string; }) {
//   if (err) {
//     console.error('error connecting: ' + err.stack);
//     return;
//   }
//   console.log('connected as id ' + connection.threadId);
// });
// const sql = postgres(process.env.POSTGRES_URL!, { ssl: 'require' });


// Create a MySQL connection pool
const pool = mysql.createPool({
  host: process.env.MYSQL_HOST,
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: process.env.MYSQL_DATABASE,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});


async function seedUsers() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS users (
      id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
      name VARCHAR(255) NOT NULL,
      email VARCHAR(255) NOT NULL UNIQUE,
      password TEXT NOT NULL
    );
  `);

	return await Promise.all(
			users.map(async (user) => {
				const hashedPassword = await bcrypt.hash(user.password, 10);
				await pool.query(`
            INSERT IGNORE INTO users (id, name, email, password)
            VALUES (?, ?, ?, ?)
				`, [user.id, user.name, user.email, hashedPassword]);
			}),
	);
}
async function seedInvoices() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS invoices (
      id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
      customer_id CHAR(36) NOT NULL,
      amount INT NOT NULL,
      status VARCHAR(255) NOT NULL,
      date DATE NOT NULL,
      FOREIGN KEY (customer_id) REFERENCES customers(id)
    );
  `);

	return await Promise.all(
			invoices.map(
					(invoice) => pool.query(`
              INSERT IGNORE INTO invoices (customer_id, amount, status, date)
              VALUES (?, ?, ?, ?)
					`, [invoice.customer_id, invoice.amount, invoice.status, invoice.date])
			),
	);
}

async function seedCustomers() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS customers (
      id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
      name VARCHAR(255) NOT NULL,
      email VARCHAR(255) NOT NULL UNIQUE,
      image_url VARCHAR(255) NOT NULL
    );
  `);

  const insertedCustomers = await Promise.all(
    customers.map(
      (customer) => pool.query(`
        INSERT IGNORE INTO customers (id, name, email, image_url)
        VALUES (?, ?, ?, ?)
      `, [customer.id, customer.name, customer.email, customer.image_url])
    ),
  );

  return insertedCustomers;
}


async function seedRevenue() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS revenue (
      month VARCHAR(4) NOT NULL UNIQUE,
      revenue INT NOT NULL
    );
  `);

	return await Promise.all(
			revenue.map(
					(rev) => pool.query(`
              INSERT IGNORE INTO revenue (month, revenue)
              VALUES (?, ?)
					`, [rev.month, rev.revenue])
			),
	);
}


export async function GET() {
  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();

    await seedUsers();
    await seedCustomers();
    await seedInvoices();
    await seedRevenue();

    await connection.commit();
    connection.release();

    return Response.json({ message: 'Database seeded successfully' });
  } catch (error: any) {
    await connection.rollback();
    connection.release();
    return Response.json({ error: error.message }, { status: 500 });
  }
}
