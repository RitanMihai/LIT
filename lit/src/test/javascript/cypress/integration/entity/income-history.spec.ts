import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('IncomeHistory e2e test', () => {
  const incomeHistoryPageUrl = '/income-history';
  const incomeHistoryPageUrlPattern = new RegExp('/income-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const incomeHistorySample = { date: '2022-03-28' };

  let incomeHistory: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/income-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/income-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/income-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (incomeHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/income-histories/${incomeHistory.id}`,
      }).then(() => {
        incomeHistory = undefined;
      });
    }
  });

  it('IncomeHistories menu should load IncomeHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('income-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('IncomeHistory').should('exist');
    cy.url().should('match', incomeHistoryPageUrlPattern);
  });

  describe('IncomeHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(incomeHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create IncomeHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/income-history/new$'));
        cy.getEntityCreateUpdateHeading('IncomeHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', incomeHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/income-histories',
          body: incomeHistorySample,
        }).then(({ body }) => {
          incomeHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/income-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/watcher/api/income-histories?page=0&size=20>; rel="last",<http://localhost/services/watcher/api/income-histories?page=0&size=20>; rel="first"',
              },
              body: [incomeHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(incomeHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details IncomeHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('incomeHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', incomeHistoryPageUrlPattern);
      });

      it('edit button click should load edit IncomeHistory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IncomeHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', incomeHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of IncomeHistory', () => {
        cy.intercept('GET', '/services/watcher/api/income-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('incomeHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', incomeHistoryPageUrlPattern);

        incomeHistory = undefined;
      });
    });
  });

  describe('new IncomeHistory page', () => {
    beforeEach(() => {
      cy.visit(`${incomeHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('IncomeHistory');
    });

    it('should create an instance of IncomeHistory', () => {
      cy.get(`[data-cy="date"]`).type('2022-03-27').should('have.value', '2022-03-27');

      cy.get(`[data-cy="totalRevenue"]`).type('42233').should('have.value', '42233');

      cy.get(`[data-cy="costOfRevenue"]`).type('5173').should('have.value', '5173');

      cy.get(`[data-cy="grossProfit"]`).type('56766').should('have.value', '56766');

      cy.get(`[data-cy="operatingExpense"]`).type('86951').should('have.value', '86951');

      cy.get(`[data-cy="operatingIncome"]`).type('61357').should('have.value', '61357');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        incomeHistory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', incomeHistoryPageUrlPattern);
    });
  });
});
