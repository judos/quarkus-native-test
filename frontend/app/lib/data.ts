import postgres from 'postgres';
import {
  CustomerField,
  CustomersTableType,
  InvoiceForm,
  InvoicesTable,
  LatestInvoiceRaw,
  Revenue,
} from './definitions';
import { formatCurrency } from './utils';
import mysql, { QueryResult, RowDataPacket } from 'mysql2/promise';

const pool = mysql.createPool({
  host: process.env.MYSQL_HOST,
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: process.env.MYSQL_DATABASE,
});

// Fetch Revenue
export async function fetchRevenue(): Promise<Revenue[]> {
  try {
    const result = await pool.query('SELECT * FROM revenue');
		const data = result[0] as RowDataPacket[];
    return data.map(x => ({
			month: x.month,
			revenue: x.revenue
		}));
  } catch (error) {
    console.error('Database Error:', error);
    throw new Error('Failed to fetch revenue data.');
  }
}

// Fetch Latest Invoices
export async function fetchLatestInvoices() {
  try {
    const [data] = await pool.query(`
      SELECT invoices.amount, customers.name, customers.image_url, customers.email, invoices.id
      FROM invoices
      JOIN customers ON invoices.customer_id = customers.id
      ORDER BY invoices.date DESC
      LIMIT 5
    `);

    const latestInvoices = data.map((invoice) => ({
      ...invoice,
      amount: formatCurrency(invoice.amount),
    }));
    return latestInvoices;
  } catch (error) {
    console.error('Database Error:', error);
    throw new Error('Failed to fetch the latest invoices.');
  }
}

// Fetch Card Data
export async function fetchCardData() {
  try {
    const invoiceCountPromise = pool.query('SELECT COUNT(*) AS count FROM invoices');
    const customerCountPromise = pool.query('SELECT COUNT(*) AS count FROM customers');
    const invoiceStatusPromise = pool.query(`
      SELECT 
        SUM(CASE WHEN status = 'paid' THEN amount ELSE 0 END) AS paid,
        SUM(CASE WHEN status = 'pending' THEN amount ELSE 0 END) AS pending
      FROM invoices
    `);

    const [invoiceCount, customerCount, invoiceStatus] = await Promise.all([
      invoiceCountPromise,
      customerCountPromise,
      invoiceStatusPromise,
    ]);

    return {
      numberOfInvoices: Number(invoiceCount[0][0].count ?? '0'),
      numberOfCustomers: Number(customerCount[0][0].count ?? '0'),
      totalPaidInvoices: formatCurrency(invoiceStatus[0][0].paid ?? '0'),
      totalPendingInvoices: formatCurrency(invoiceStatus[0][0].pending ?? '0'),
    };
  } catch (error) {
    console.error('Database Error:', error);
    throw new Error('Failed to fetch card data.');
  }
}

// Fetch Filtered Invoices
export async function fetchFilteredInvoices(query, currentPage) {
  const offset = (currentPage - 1) * ITEMS_PER_PAGE;

  try {
    const [invoices] = await pool.query(`
      SELECT 
        invoices.id,
        invoices.amount,
        invoices.date,
        invoices.status,
        customers.name,
        customers.email,
        customers.image_url
      FROM invoices
      JOIN customers ON invoices.customer_id = customers.id
      WHERE 
        LOWER(customers.name) LIKE LOWER(?) OR
        LOWER(customers.email) LIKE LOWER(?) OR
        LOWER(invoices.amount) LIKE LOWER(?) OR
        LOWER(invoices.date) LIKE LOWER(?) OR
        LOWER(invoices.status) LIKE LOWER(?)
      ORDER BY invoices.date DESC
      LIMIT ? OFFSET ?
    `, [
      `%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`,
      ITEMS_PER_PAGE, offset
    ]);

    return invoices;
  } catch (error) {
    console.error('Database Error:', error);
    throw new Error('Failed to fetch invoices.');
  }
}

// Fetch Invoices Pages
export async function fetchInvoicesPages(query) {
  try {
    const [data] = await pool.query(`
      SELECT COUNT(*) AS count
      FROM invoices
      JOIN customers ON invoices.customer_id = customers.id
      WHERE 
        LOWER(customers.name) LIKE LOWER(?) OR
        LOWER(customers.email) LIKE LOWER(?) OR
        LOWER(invoices.amount) LIKE LOWER(?) OR
        LOWER(invoices.date) LIKE LOWER(?) OR
        LOWER(invoices.status) LIKE LOWER(?)
    `, [
      `%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`
    ]);

    const totalPages = Math.ceil(Number(data[0].count) / ITEMS_PER_PAGE);
    return totalPages;
  } catch (error) {
    console.error('Database Error:', error);
    throw new Error('Failed to fetch total number of invoices.');
  }
}

// Fetch Invoice by ID
export async function fetchInvoiceById(id) {
  try {
    const [data] = await pool.query(`
      SELECT 
        invoices.id,
        invoices.customer_id,
        invoices.amount,
        invoices.status
      FROM invoices
      WHERE invoices.id = ?
    `, [id]);

    const invoice = data.map((invoice) => ({
      ...invoice,
      amount: invoice.amount / 100,
    }));

    return invoice[0];
  } catch (error) {
    console.error('Database Error:', error);
    throw new Error('Failed to fetch invoice.');
  }
}

// Fetch Customers
export async function fetchCustomers() {
  try {
    const [customers] = await pool.query(`
      SELECT id, name FROM customers ORDER BY name ASC
    `);
    return customers;
  } catch (err) {
    console.error('Database Error:', err);
    throw new Error('Failed to fetch all customers.');
  }
}

// Fetch Filtered Customers
export async function fetchFilteredCustomers(query) {
  try {
    const [data] = await pool.query(`
      SELECT 
        customers.id,
        customers.name,
        customers.email,
        customers.image_url,
        COUNT(invoices.id) AS total_invoices,
        SUM(CASE WHEN invoices.status = 'pending' THEN invoices.amount ELSE 0 END) AS total_pending,
        SUM(CASE WHEN invoices.status = 'paid' THEN invoices.amount ELSE 0 END) AS total_paid
      FROM customers
      LEFT JOIN invoices ON customers.id = invoices.customer_id
      WHERE 
        LOWER(customers.name) LIKE LOWER(?) OR
        LOWER(customers.email) LIKE LOWER(?)
      GROUP BY customers.id, customers.name, customers.email, customers.image_url
      ORDER BY customers.name ASC
    `, [`%${query}%`, `%${query}%`]);

    const customers = data.map((customer) => ({
      ...customer,
      total_pending: formatCurrency(customer.total_pending),
      total_paid: formatCurrency(customer.total_paid),
    }));

    return customers;
  } catch (err) {
    console.error('Database Error:', err);
    throw new Error('Failed to fetch customer table.');
  }
}


//
// const sql = postgres(process.env.POSTGRES_URL!, { ssl: 'require' });
//
// export async function fetchRevenue() {
//   try {
//     // Artificially delay a response for demo purposes.
//     // Don't do this in production :)
//
//     // console.log('Fetching revenue data...');
//     // await new Promise((resolve) => setTimeout(resolve, 3000));
//
//     const data = await sql<Revenue[]>`SELECT * FROM revenue`;
//
//     // console.log('Data fetch completed after 3 seconds.');
//
//     return data;
//   } catch (error) {
//     console.error('Database Error:', error);
//     throw new Error('Failed to fetch revenue data.');
//   }
// }
//
// export async function fetchLatestInvoices() {
//   try {
//     const data = await sql<LatestInvoiceRaw[]>`
//       SELECT invoices.amount, customers.name, customers.image_url, customers.email, invoices.id
//       FROM invoices
//       JOIN customers ON invoices.customer_id = customers.id
//       ORDER BY invoices.date DESC
//       LIMIT 5`;
//
//     const latestInvoices = data.map((invoice) => ({
//       ...invoice,
//       amount: formatCurrency(invoice.amount),
//     }));
//     return latestInvoices;
//   } catch (error) {
//     console.error('Database Error:', error);
//     throw new Error('Failed to fetch the latest invoices.');
//   }
// }
//
// export async function fetchCardData() {
//   try {
//     // You can probably combine these into a single SQL query
//     // However, we are intentionally splitting them to demonstrate
//     // how to initialize multiple queries in parallel with JS.
//     const invoiceCountPromise = sql`SELECT COUNT(*) FROM invoices`;
//     const customerCountPromise = sql`SELECT COUNT(*) FROM customers`;
//     const invoiceStatusPromise = sql`SELECT
//          SUM(CASE WHEN status = 'paid' THEN amount ELSE 0 END) AS "paid",
//          SUM(CASE WHEN status = 'pending' THEN amount ELSE 0 END) AS "pending"
//          FROM invoices`;
//
//     const data = await Promise.all([
//       invoiceCountPromise,
//       customerCountPromise,
//       invoiceStatusPromise,
//     ]);
//
//     const numberOfInvoices = Number(data[0][0].count ?? '0');
//     const numberOfCustomers = Number(data[1][0].count ?? '0');
//     const totalPaidInvoices = formatCurrency(data[2][0].paid ?? '0');
//     const totalPendingInvoices = formatCurrency(data[2][0].pending ?? '0');
//
//     return {
//       numberOfCustomers,
//       numberOfInvoices,
//       totalPaidInvoices,
//       totalPendingInvoices,
//     };
//   } catch (error) {
//     console.error('Database Error:', error);
//     throw new Error('Failed to fetch card data.');
//   }
// }
//
// const ITEMS_PER_PAGE = 6;
// export async function fetchFilteredInvoices(
//   query: string,
//   currentPage: number,
// ) {
//   const offset = (currentPage - 1) * ITEMS_PER_PAGE;
//
//   try {
//     const invoices = await sql<InvoicesTable[]>`
//       SELECT
//         invoices.id,
//         invoices.amount,
//         invoices.date,
//         invoices.status,
//         customers.name,
//         customers.email,
//         customers.image_url
//       FROM invoices
//       JOIN customers ON invoices.customer_id = customers.id
//       WHERE
//         customers.name ILIKE ${`%${query}%`} OR
//         customers.email ILIKE ${`%${query}%`} OR
//         invoices.amount::text ILIKE ${`%${query}%`} OR
//         invoices.date::text ILIKE ${`%${query}%`} OR
//         invoices.status ILIKE ${`%${query}%`}
//       ORDER BY invoices.date DESC
//       LIMIT ${ITEMS_PER_PAGE} OFFSET ${offset}
//     `;
//
//     return invoices;
//   } catch (error) {
//     console.error('Database Error:', error);
//     throw new Error('Failed to fetch invoices.');
//   }
// }
//
// export async function fetchInvoicesPages(query: string) {
//   try {
//     const data = await sql`SELECT COUNT(*)
//     FROM invoices
//     JOIN customers ON invoices.customer_id = customers.id
//     WHERE
//       customers.name ILIKE ${`%${query}%`} OR
//       customers.email ILIKE ${`%${query}%`} OR
//       invoices.amount::text ILIKE ${`%${query}%`} OR
//       invoices.date::text ILIKE ${`%${query}%`} OR
//       invoices.status ILIKE ${`%${query}%`}
//   `;
//
//     const totalPages = Math.ceil(Number(data[0].count) / ITEMS_PER_PAGE);
//     return totalPages;
//   } catch (error) {
//     console.error('Database Error:', error);
//     throw new Error('Failed to fetch total number of invoices.');
//   }
// }
//
// export async function fetchInvoiceById(id: string) {
//   try {
//     const data = await sql<InvoiceForm[]>`
//       SELECT
//         invoices.id,
//         invoices.customer_id,
//         invoices.amount,
//         invoices.status
//       FROM invoices
//       WHERE invoices.id = ${id};
//     `;
//
//     const invoice = data.map((invoice) => ({
//       ...invoice,
//       // Convert amount from cents to dollars
//       amount: invoice.amount / 100,
//     }));
//
//     return invoice[0];
//   } catch (error) {
//     console.error('Database Error:', error);
//     throw new Error('Failed to fetch invoice.');
//   }
// }
//
// export async function fetchCustomers() {
//   try {
//     const customers = await sql<CustomerField[]>`
//       SELECT
//         id,
//         name
//       FROM customers
//       ORDER BY name ASC
//     `;
//
//     return customers;
//   } catch (err) {
//     console.error('Database Error:', err);
//     throw new Error('Failed to fetch all customers.');
//   }
// }
//
// export async function fetchFilteredCustomers(query: string) {
//   try {
//     const data = await sql<CustomersTableType[]>`
// 		SELECT
// 		  customers.id,
// 		  customers.name,
// 		  customers.email,
// 		  customers.image_url,
// 		  COUNT(invoices.id) AS total_invoices,
// 		  SUM(CASE WHEN invoices.status = 'pending' THEN invoices.amount ELSE 0 END) AS total_pending,
// 		  SUM(CASE WHEN invoices.status = 'paid' THEN invoices.amount ELSE 0 END) AS total_paid
// 		FROM customers
// 		LEFT JOIN invoices ON customers.id = invoices.customer_id
// 		WHERE
// 		  customers.name ILIKE ${`%${query}%`} OR
//         customers.email ILIKE ${`%${query}%`}
// 		GROUP BY customers.id, customers.name, customers.email, customers.image_url
// 		ORDER BY customers.name ASC
// 	  `;
//
//     const customers = data.map((customer) => ({
//       ...customer,
//       total_pending: formatCurrency(customer.total_pending),
//       total_paid: formatCurrency(customer.total_paid),
//     }));
//
//     return customers;
//   } catch (err) {
//     console.error('Database Error:', err);
//     throw new Error('Failed to fetch customer table.');
//   }
// }
